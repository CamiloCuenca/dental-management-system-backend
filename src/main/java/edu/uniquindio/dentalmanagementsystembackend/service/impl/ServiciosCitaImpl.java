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
            // Buscar paciente
            User paciente = userRepository.findById(dto.pacienteId())
                    .orElseThrow(() -> new IllegalArgumentException("No se encontró el paciente. Por favor, verifique el ID."));

            // Buscar odontólogo
            User doctor = userRepository.findById(dto.odontologoId())
                    .orElseThrow(() -> new IllegalArgumentException("No se encontró el odontólogo. Por favor, verifique el ID."));

            // Validar rol del odontólogo de manera más amigable
            if (!doctor.getAccount().getRol().equals(Rol.DOCTOR)) {
                throw new IllegalArgumentException("El usuario seleccionado no es un odontólogo. Por favor, seleccione un odontólogo válido.");
            }

            // Buscar tipo de cita
            TipoCita tipoCita = tipoCitaRepository.findById(dto.tipoCitaId())
                    .orElseThrow(() -> new IllegalArgumentException("No se encontró el tipo de cita. Por favor, seleccione un tipo válido."));

            // Validar especialidad del odontólogo de manera más flexible
            Especialidad especialidad = tipoCita.getEspecialidadRequerida();
            if (!doctor.getEspecialidades().contains(especialidad)) {
                throw new IllegalArgumentException(
                    String.format("El odontólogo %s no tiene la especialidad %s requerida para este tipo de cita. " +
                        "Por favor, seleccione otro odontólogo con esta especialidad.", 
                        doctor.getName(), especialidad.getNombre())
                );
            }

            // Validar disponibilidad con margen de tiempo
            LocalDateTime fecha = LocalDateTime.ofInstant(dto.fechaHora(), ZoneId.systemDefault());
            
            // Validar horario de trabajo
            List<DisponibilidadDoctor> disponibilidades = disponibilidadDoctorRepository
                .findByDoctor_IdNumberAndDiaSemanaAndEstado(doctor.getIdNumber(), fecha.getDayOfWeek(), "ACTIVO");
            
            if (disponibilidades.isEmpty()) {
                throw new IllegalArgumentException(
                    String.format("El doctor %s no tiene disponibilidad programada para los %s. " +
                        "Por favor, seleccione otro día de la semana.", 
                        doctor.getName(), 
                        fecha.getDayOfWeek().toString().toLowerCase())
                );
            }

            // Verificar si la hora está dentro del horario de trabajo
            boolean dentroHorarioTrabajo = disponibilidades.stream()
                .anyMatch(disp -> 
                    !fecha.toLocalTime().isBefore(disp.getHoraInicio()) && 
                    !fecha.toLocalTime().isAfter(disp.getHoraFin())
                );

            if (!dentroHorarioTrabajo) {
                String horariosDisponibles = disponibilidades.stream()
                    .map(disp -> String.format("%s - %s", 
                        disp.getHoraInicio().format(DateTimeFormatter.ofPattern("HH:mm")),
                        disp.getHoraFin().format(DateTimeFormatter.ofPattern("HH:mm"))))
                    .collect(Collectors.joining(", "));

                throw new IllegalArgumentException(
                    String.format("El horario seleccionado está fuera del horario de trabajo del doctor %s. " +
                        "Horarios disponibles para los %s: %s", 
                        doctor.getName(),
                        fecha.getDayOfWeek().toString().toLowerCase(),
                        horariosDisponibles)
                );
            }

            // Verificar si ya existe una cita en ese horario
            boolean citaExistente = citasRepository.existsByDoctorAndFechaHoraBetween(
                doctor,
                fecha.atZone(ZoneId.systemDefault()).toInstant(),
                fecha.plusMinutes(tipoCita.getDuracionMinutos()).atZone(ZoneId.systemDefault()).toInstant()
            );
            
            if (citaExistente) {
                throw new IllegalArgumentException(
                    String.format("Ya existe una cita programada para el doctor %s en el horario %s. " +
                        "Por favor, seleccione otro horario.", 
                        doctor.getName(), 
                        fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                );
            }

            boolean disponible = disponibilidadDoctorRepository.existsByDoctor_IdNumberAndFecha(
                dto.odontologoId(),
                fecha.getDayOfWeek(),
                fecha.toLocalTime()
            );

            if (!disponible) {
                // Buscar horarios alternativos disponibles
                List<LocalTime> horariosAlternativos = encontrarHorariosAlternativos(doctor, fecha.toLocalDate(), tipoCita);
                
                if (!horariosAlternativos.isEmpty()) {
                    String horariosSugeridos = horariosAlternativos.stream()
                        .map(time -> time.format(DateTimeFormatter.ofPattern("HH:mm")))
                        .collect(Collectors.joining(", "));
                    
                    throw new IllegalArgumentException(
                        String.format("El odontólogo no está disponible en el horario solicitado. " +
                            "Horarios alternativos disponibles para el mismo día: %s", horariosSugeridos)
                    );
                } else {
                    throw new IllegalArgumentException(
                        "El odontólogo no está disponible en el horario solicitado. " +
                        "Por favor, seleccione otro día u otro odontólogo."
                    );
                }
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
        } catch (IllegalArgumentException e) {
            logger.warn("Error al crear cita: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error inesperado al crear cita", e);
            throw new RuntimeException("Ocurrió un error al crear la cita. Por favor, intente nuevamente más tarde.");
        }
    }

    /**
     * Encuentra horarios alternativos disponibles para un doctor en una fecha específica
     */
    private List<LocalTime> encontrarHorariosAlternativos(User doctor, LocalDate fecha, TipoCita tipoCita) {
        List<LocalTime> horariosDisponibles = new ArrayList<>();
        DayOfWeek diaSemana = fecha.getDayOfWeek();
        
        // Obtener todas las disponibilidades del doctor para ese día
        List<DisponibilidadDoctor> disponibilidades = disponibilidadDoctorRepository
            .findByDoctor_IdNumberAndDiaSemanaAndEstado(doctor.getIdNumber(), diaSemana, "ACTIVO");
        
        for (DisponibilidadDoctor disponibilidad : disponibilidades) {
            LocalTime horaActual = disponibilidad.getHoraInicio();
            LocalTime horaFin = disponibilidad.getHoraFin();
            
            // Verificar cada intervalo de tiempo
            while (horaActual.plusMinutes(tipoCita.getDuracionMinutos()).isBefore(horaFin) || 
                   horaActual.plusMinutes(tipoCita.getDuracionMinutos()).equals(horaFin)) {
                
                // Verificar si el horario está disponible
                boolean horarioDisponible = !citasRepository.existsByDoctorAndFechaHoraBetween(
                    doctor,
                    fecha.atTime(horaActual).atZone(ZoneId.systemDefault()).toInstant(),
                    fecha.atTime(horaActual.plusMinutes(tipoCita.getDuracionMinutos())).atZone(ZoneId.systemDefault()).toInstant()
                );
                
                if (horarioDisponible) {
                    horariosDisponibles.add(horaActual);
                }
                
                horaActual = horaActual.plusMinutes(30); // Intervalo de 30 minutos
            }
        }
        
        return horariosDisponibles;
    }
}