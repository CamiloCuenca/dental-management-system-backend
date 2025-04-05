package edu.uniquindio.dentalmanagementsystembackend.service.impl;

import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoCitas;
import edu.uniquindio.dentalmanagementsystembackend.Enum.Rol;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.CrearCitaDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.email.CitaEmailDTO;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.User;
import edu.uniquindio.dentalmanagementsystembackend.entity.Cita;
import edu.uniquindio.dentalmanagementsystembackend.entity.DisponibilidadDoctor;
import edu.uniquindio.dentalmanagementsystembackend.entity.Especialidad;
import edu.uniquindio.dentalmanagementsystembackend.entity.TipoCita;
import edu.uniquindio.dentalmanagementsystembackend.exception.HistorialException;
import edu.uniquindio.dentalmanagementsystembackend.repository.CitasRepository;
import edu.uniquindio.dentalmanagementsystembackend.repository.CuentaRepository;
import edu.uniquindio.dentalmanagementsystembackend.repository.DisponibilidadDoctorRepository;
import edu.uniquindio.dentalmanagementsystembackend.repository.EspecialidadRepository;
import edu.uniquindio.dentalmanagementsystembackend.repository.TipoCitaRepository;
import edu.uniquindio.dentalmanagementsystembackend.repository.UserRepository;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.EmailService;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.ServiciosCitas;
import edu.uniquindio.dentalmanagementsystembackend.util.DateUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de gestión de citas dentales.
 * Esta clase maneja toda la lógica de negocio relacionada con las citas,
 * incluyendo su creación, consulta, modificación y cancelación.
 */
@Service
@org.springframework.transaction.annotation.Transactional
@RequiredArgsConstructor
@Slf4j
public class ServiciosCitaImpl implements ServiciosCitas {

    private static final Logger logger = LoggerFactory.getLogger(ServiciosCitaImpl.class);

    // Repositorio para operaciones CRUD de citas
    @Autowired
    private CitasRepository citasRepository;

    // Repositorio para operaciones relacionadas con cuentas de usuario
    @Autowired
    private CuentaRepository cuentaRepository;

    // Repositorio para operaciones CRUD de usuarios
    @Autowired
    private UserRepository userRepository;

    // Repositorio para operaciones CRUD de disponibilidad de doctores
    @Autowired
    private DisponibilidadDoctorRepository disponibilidadDoctorRepository;

    // Repositorio para operaciones CRUD de tipos de cita
    @Autowired
    private TipoCitaRepository tipoCitaRepository;

    // Repositorio para operaciones CRUD de especialidades
    @Autowired
    private EspecialidadRepository especialidadRepository;

    // Servicio para envío de correos electrónicos
    @Autowired
    private EmailService emailService;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Cita crearCita(CrearCitaDTO dto) {
        try {
            // ✅ VALIDACIONES BÁSICAS: verificar existencia y roles correctos

            User paciente = userRepository.findById(dto.pacienteId())
                    .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado."));

            User doctor = userRepository.findById(dto.odontologoId())
                    .orElseThrow(() -> new IllegalArgumentException("Odontólogo no encontrado."));

            if (!doctor.getAccount().getRol().equals(Rol.DOCTOR)) {
                throw new IllegalArgumentException("El usuario no es un odontólogo.");
            }

            TipoCita tipoCita = tipoCitaRepository.findById(dto.tipoCitaId())
                    .orElseThrow(() -> new IllegalArgumentException("Tipo de cita no encontrado."));

            // 🔐 VALIDACIONES CRÍTICAS: lógica de negocio que asegura integridad

            Especialidad especialidad = tipoCita.getEspecialidadRequerida();
            if (!doctor.getEspecialidades().contains(especialidad)) {
                throw new IllegalArgumentException("El odontólogo no tiene la especialidad requerida.");
            }

            LocalDateTime fecha = LocalDateTime.ofInstant(dto.fechaHora(), ZoneId.systemDefault());

            List<DisponibilidadDoctor> disponibilidades = disponibilidadDoctorRepository
                    .findByDoctor_IdNumberAndDiaSemanaAndEstado(doctor.getIdNumber(), fecha.getDayOfWeek(), "ACTIVO");

            if (disponibilidades.isEmpty()) {
                throw new IllegalArgumentException("El odontólogo no tiene disponibilidad ese día.");
            }

            boolean dentroHorario = disponibilidades.stream()
                    .anyMatch(disp ->
                            !fecha.toLocalTime().isBefore(disp.getHoraInicio()) &&
                                    !fecha.toLocalTime().isAfter(disp.getHoraFin())
                    );

            if (!dentroHorario) {
                String horariosDisponibles = disponibilidades.stream()
                        .map(disp -> String.format("%s - %s",
                                disp.getHoraInicio().format(DateTimeFormatter.ofPattern("HH:mm")),
                                disp.getHoraFin().format(DateTimeFormatter.ofPattern("HH:mm"))))
                        .collect(Collectors.joining(", "));

                throw new IllegalArgumentException(
                        String.format("Hora fuera del horario de atención del odontólogo. Horarios disponibles: %s",
                                horariosDisponibles)
                );
            }

            boolean citaExistente = citasRepository.existsByDoctorAndFechaHoraBetween(
                    doctor,
                    fecha.atZone(ZoneId.systemDefault()).toInstant(),
                    fecha.plusMinutes(tipoCita.getDuracionMinutos()).atZone(ZoneId.systemDefault()).toInstant()
            );

            if (citaExistente) {
                throw new IllegalArgumentException("Ya existe una cita en ese horario.");
            }

            Cita cita = new Cita();
            cita.setPaciente(paciente);
            cita.setDoctor(doctor);
            cita.setFechaHora(fecha.atZone(ZoneId.systemDefault()).toInstant());
            cita.setEstado(EstadoCitas.PENDIENTE);
            cita.setTipoCita(tipoCita);

            Cita citaGuardada = citasRepository.save(cita);

            // ✉️ ENVÍO DE CORREO (opcional)

            try {
                CitaEmailDTO emailDTO = new CitaEmailDTO(
                        paciente.getAccount().getEmail(),
                        paciente.getName(),
                        tipoCita.getNombre(),
                        DateUtil.formatearFechaHora(cita.getFechaHora()),
                        doctor.getName()
                );

                emailService.enviarCorreoCita(emailDTO);
            } catch (Exception e) {
                logger.warn("No se pudo enviar el correo de confirmación: {}", e.getMessage());
            }

            return citaGuardada;

        } catch (IllegalArgumentException e) {
            logger.warn("Error al crear cita: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error inesperado al crear cita", e);
            throw new RuntimeException("Ocurrió un error al crear la cita. Por favor, intente nuevamente más tarde.");
        }
    }


}