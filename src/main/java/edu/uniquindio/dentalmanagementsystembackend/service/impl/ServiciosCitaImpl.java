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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Implementación del servicio de gestión de citas dentales.
 * Esta clase maneja toda la lógica de negocio relacionada con las citas,
 * incluyendo su creación, consulta, modificación y cancelación.
 */
@Service
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

        // Buscar paciente
        User paciente = userRepository.findById(dto.pacienteId())
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado"));

        // Buscar odontólogo
        User doctor = userRepository.findById(dto.odontologoId())
                .orElseThrow(() -> new IllegalArgumentException("Odontólogo no encontrado"));

        // Validar rol del odontólogo
        if (!doctor.getAccount().getRol().equals(Rol.DOCTOR)) {
            throw new IllegalArgumentException("El usuario no tiene el rol de DOCTOR");
        }

        // Buscar tipo de cita
        TipoCita tipoCita = tipoCitaRepository.findById(dto.tipoCitaId())
                .orElseThrow(() -> new IllegalArgumentException("Tipo de cita no encontrado"));

        // Validar especialidad del odontólogo
        Especialidad especialidad = tipoCita.getEspecialidadRequerida();
        if (!doctor.getEspecialidades().contains(especialidad)) {
            throw new IllegalArgumentException("El odontólogo no tiene la especialidad requerida para este tipo de cita");
        }

        // Validar disponibilidad
        LocalDateTime fecha = LocalDateTime.ofInstant(dto.fechaHora(), ZoneId.systemDefault());
        boolean disponible = disponibilidadDoctorRepository.existsByDoctor_IdNumberAndFecha(
            dto.odontologoId(),
            fecha.getDayOfWeek(),
            fecha.toLocalTime()
        );
        if (!disponible) {
            throw new IllegalArgumentException("El odontólogo no está disponible para esta fecha y hora");
        }

        // Crear cita
        Cita cita = new Cita();
        cita.setPaciente(paciente);
        cita.setDoctor(doctor);
        cita.setFechaHora(fecha.atZone(ZoneId.systemDefault()).toInstant());
        cita.setEstado(EstadoCitas.PENDIENTE);
        cita.setTipoCita(tipoCita);

        // Guardar cita
        Cita citaGuardada = citasRepository.save(cita);

        // Enviar correo de confirmación (opcional)
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
    }
}