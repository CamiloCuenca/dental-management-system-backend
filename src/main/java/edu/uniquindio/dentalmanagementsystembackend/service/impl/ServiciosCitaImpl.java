package edu.uniquindio.dentalmanagementsystembackend.service.impl;

import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoCitas;
import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoDisponibilidad;
import edu.uniquindio.dentalmanagementsystembackend.Enum.Rol;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.CrearCitaDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.CrearCitaNoAutenticadaDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.DisponibilidadDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.DoctorEspecialidadDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.EditarCitaAdminDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.EditarCitaNoAutenticadaAdminDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.EditarCitaPacienteDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.FechaDisponibleDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.HorarioDisponibleDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.email.CitaEmailDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.CitaDTO;
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

import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.ServiciosDisponibilidadDoctor;

/**
 * Implementación del servicio de gestión de citas dentales.
 * Esta clase maneja toda la lógica de negocio relacionada con las citas,
 * incluyendo su creación, consulta, modificación y cancelación.
 */
@Service
@Transactional
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

    @Autowired
    private ServiciosDisponibilidadDoctor serviciosDisponibilidadDoctor;

    @PersistenceContext
    private EntityManager entityManager;

    // ==============================================
    // MÉTODOS PARA CITAS AUTENTICADAS (PACIENTES REGISTRADOS)
    // ==============================================

    /**
     * Crea una nueva cita para un paciente autenticado.
     * Valida la existencia del paciente y doctor, verifica disponibilidad y
     * horarios.
     */
    @Override
    @Transactional
    public Cita crearCita(CrearCitaDTO crearCitaDTO) {
        System.out.println("\n=== Creando nueva cita ===");
        System.out.println("Paciente ID: " + crearCitaDTO.pacienteId());
        System.out.println("Doctor ID: " + crearCitaDTO.doctorId());
        System.out.println("Fecha: " + crearCitaDTO.fecha());
        System.out.println("Hora: " + crearCitaDTO.hora());
        System.out.println("Tipo de cita ID: " + crearCitaDTO.tipoCitaId());

        try {
            // Validar que el paciente exista
            User paciente = userRepository.findByIdNumber(crearCitaDTO.pacienteId())
                    .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

            // Validar que el doctor exista
            User doctor = userRepository.findByIdNumber(crearCitaDTO.doctorId())
                    .orElseThrow(() -> new RuntimeException("Doctor no encontrado"));

            // Validar que el usuario sea un doctor
            if (!doctor.getAccount().getRol().equals(Rol.DOCTOR)) {
                throw new RuntimeException("El usuario especificado no es un doctor");
            }

            // Validar que la fecha no sea en el pasado
            LocalDateTime fechaHoraCita = LocalDateTime.of(crearCitaDTO.fecha(), crearCitaDTO.hora());
            if (fechaHoraCita.isBefore(LocalDateTime.now())) {
                throw new RuntimeException("No se pueden crear citas en fechas pasadas");
            }

            // Validar disponibilidad del doctor
            if (!serviciosDisponibilidadDoctor.validarDisponibilidadDoctor(
                    crearCitaDTO.doctorId(),
                    crearCitaDTO.fecha(),
                    crearCitaDTO.hora())) {
                throw new RuntimeException("El doctor no está disponible en ese horario");
            }

            // Validar que no exista otra cita en el mismo horario
            if (citasRepository.existsByDoctorAndFechaHora(doctor,
                    fechaHoraCita.atZone(ZoneId.systemDefault()).toInstant())) {
                throw new RuntimeException("Ya existe una cita programada para ese horario");
            }

            // Obtener el tipo de cita
            TipoCita tipoCita = tipoCitaRepository.findById(crearCitaDTO.tipoCitaId())
                    .orElseThrow(() -> new RuntimeException("Tipo de cita no encontrado"));

            // Crear la cita
            Cita cita = new Cita();
            cita.setPaciente(paciente);
            cita.setDoctor(doctor);
            cita.setFechaHora(fechaHoraCita.atZone(ZoneId.systemDefault()).toInstant());
            cita.setEstado(EstadoCitas.CONFIRMADA);
            cita.setTipoCita(tipoCita);

            // Guardar la cita
            Cita citaGuardada = citasRepository.save(cita);
            System.out.println("Cita creada exitosamente con ID: " + citaGuardada.getId());

            // Enviar correo de confirmación
            enviarCorreoConfirmacionCita(citaGuardada);

            return citaGuardada;
        } catch (Exception e) {
            logger.error("Error al crear la cita", e);
            throw new RuntimeException("Error al crear la cita: " + e.getMessage());
        }
    }

    /**
     * Permite a un paciente autenticado editar su cita existente.
     * Solo permite modificar fecha y hora, manteniendo el mismo doctor y tipo de
     * cita.
     */
    @Override
    @Transactional
    public Cita editarCitaPaciente(Long idCita, EditarCitaPacienteDTO dto) {
        System.out.println("\n=== Editando cita ID: " + idCita + " (Paciente) ===");

        try {
            Cita cita = citasRepository.findById(idCita)
                    .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));

            if (dto.fecha() == null || dto.hora() == null) {
                throw new IllegalArgumentException("Fecha y hora no pueden ser nulas");
            }

            LocalDateTime nuevaFechaHora = LocalDateTime.of(dto.fecha(), dto.hora());
            Instant instant = nuevaFechaHora.atZone(ZoneId.systemDefault()).toInstant();

            if (nuevaFechaHora.isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("La fecha y hora no pueden ser en el pasado");
            }

            if (cita.getEstado() == EstadoCitas.CANCELADA || cita.getEstado() == EstadoCitas.COMPLETADA) {
                throw new IllegalArgumentException("No se puede editar una cita cancelada o completada");
            }

            cita.setFechaHora(instant);
            cita.setEstado(EstadoCitas.PENDIENTE);

            cita = citasRepository.save(cita);
            System.out.println("Cita actualizada exitosamente");

            return cita;
        } catch (IllegalArgumentException e) {
            logger.warn("Error de validación al editar cita: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error inesperado al editar cita", e);
            e.printStackTrace(); // Agrega esto temporalmente
            throw e; // Cambia esto temporalmente para ver la excepción real
        }
    }

    /**
     * Permite a un paciente autenticado cancelar su cita.
     * Envía notificación por correo electrónico.
     */
    @Override
    @Transactional
    public void cancelarCita(Long idCita) {
        System.out.println("\n=== Cancelando cita ID: " + idCita + " ===");
        try {
            Cita cita = citasRepository.findById(idCita)
                    .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));

            // Validar que la cita no esté ya cancelada o completada
            if (cita.getEstado() == EstadoCitas.CANCELADA) {
                throw new IllegalArgumentException("La cita ya está cancelada");
            }
            if (cita.getEstado() == EstadoCitas.COMPLETADA) {
                throw new IllegalArgumentException("No se puede cancelar una cita completada");
            }

            // Cancelar la cita
            cita.setEstado(EstadoCitas.CANCELADA);
            citasRepository.save(cita);
            System.out.println("Cita cancelada exitosamente");

            // Enviar correo de cancelación
            try {
                LocalDateTime fechaHoraLocal = cita.getFechaHora().atZone(ZoneId.systemDefault()).toLocalDateTime();
                emailService.enviarCorreoCancelacionCita(
                        cita.getPaciente().getAccount().getEmail(),
                        cita.getDoctor().getName() + " " + cita.getDoctor().getLastName(),
                        fechaHoraLocal);
            } catch (Exception e) {
                logger.warn("No se pudo enviar el correo de cancelación: {}", e.getMessage());
            }
        } catch (IllegalArgumentException e) {
            logger.warn("Error de validación al cancelar cita: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error inesperado al cancelar cita", e);
            throw new RuntimeException("Error al cancelar la cita. Por favor, intente nuevamente.");
        }
    }

    /**
     * Confirma una cita pendiente de un paciente autenticado.
     * Envía notificación por correo electrónico.
     */
    @Override
    @Transactional
    public void confirmarCita(Long idCita) {
        System.out.println("\n=== Confirmando cita ID: " + idCita + " ===");
        try {
            Cita cita = citasRepository.findById(idCita)
                    .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));

            // Validar que la cita esté pendiente
            if (cita.getEstado() != EstadoCitas.PENDIENTE) {
                throw new IllegalArgumentException("Solo se pueden confirmar citas pendientes");
            }

            // Confirmar la cita
            cita.setEstado(EstadoCitas.CONFIRMADA);
            citasRepository.save(cita);
            System.out.println("Cita confirmada exitosamente");

            // Enviar correo de confirmación
            try {
                LocalDateTime fechaHoraLocal = cita.getFechaHora().atZone(ZoneId.systemDefault()).toLocalDateTime();
                emailService.enviarCorreoConfirmacionCita(
                        cita.getPaciente().getAccount().getEmail(),
                        cita.getDoctor().getName() + " " + cita.getDoctor().getLastName(),
                        fechaHoraLocal);
            } catch (Exception e) {
                logger.warn("No se pudo enviar el correo de confirmación: {}", e.getMessage());
            }
        } catch (IllegalArgumentException e) {
            logger.warn("Error de validación al confirmar cita: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error inesperado al confirmar cita", e);
            throw new RuntimeException("Error al confirmar la cita. Por favor, intente nuevamente.");
        }
    }

    /**
     * Marca una cita como completada.
     * Envía notificación por correo electrónico.
     */
    @Override
    @Transactional
    public void completarCita(Long idCita) {
        System.out.println("\n=== Completando cita ID: " + idCita + " ===");
        try {
            Cita cita = citasRepository.findById(idCita)
                    .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));

            // Validar que la cita esté confirmada
            if (cita.getEstado() != EstadoCitas.CONFIRMADA) {
                throw new IllegalArgumentException("Solo se pueden completar citas confirmadas");
            }

            // Completar la cita
            cita.setEstado(EstadoCitas.COMPLETADA);
            citasRepository.save(cita);
            System.out.println("Cita marcada como completada exitosamente");

            // Enviar correo de cita completada
            try {
                LocalDateTime fechaHoraLocal = cita.getFechaHora().atZone(ZoneId.systemDefault()).toLocalDateTime();
                emailService.enviarCorreoCitaCompletada(
                        cita.getPaciente().getAccount().getEmail(),
                        cita.getDoctor().getName() + " " + cita.getDoctor().getLastName(),
                        fechaHoraLocal);
            } catch (Exception e) {
                logger.warn("No se pudo enviar el correo de cita completada: {}", e.getMessage());
            }
        } catch (IllegalArgumentException e) {
            logger.warn("Error de validación al completar cita: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error inesperado al completar cita", e);
            throw new RuntimeException("Error al completar la cita. Por favor, intente nuevamente.");
        }
    }

    // ==============================================
    // MÉTODOS PARA CITAS NO AUTENTICADAS
    // ==============================================

    /**
     * Crea una nueva cita para un paciente no autenticado.
     * Almacena los datos del paciente directamente en la cita.
     */
    @Override
    @Transactional
    public Cita crearCitaNoAutenticada(CrearCitaNoAutenticadaDTO crearCitaNoAutenticadaDTO) {
        System.out.println("\n=== Creando nueva cita no autenticada ===");
        System.out.println("Nombre Paciente: " + crearCitaNoAutenticadaDTO.nombrePaciente());
        System.out.println("Número Identificación: " + crearCitaNoAutenticadaDTO.pacienteId());
        System.out.println("Doctor ID: " + crearCitaNoAutenticadaDTO.doctorId());
        System.out.println("Fecha: " + crearCitaNoAutenticadaDTO.fecha());
        System.out.println("Hora: " + crearCitaNoAutenticadaDTO.hora());
        System.out.println("Tipo de cita ID: " + crearCitaNoAutenticadaDTO.tipoCitaId());

        try {
            // Validar que el doctor exista
            User doctor = userRepository.findByIdNumber(crearCitaNoAutenticadaDTO.doctorId())
                    .orElseThrow(() -> new RuntimeException("Doctor no encontrado"));

            // Validar que el usuario sea un doctor
            if (!doctor.getAccount().getRol().equals(Rol.DOCTOR)) {
                throw new RuntimeException("El usuario especificado no es un doctor");
            }

            // Validar que la fecha no sea en el pasado
            LocalDateTime fechaHoraCita = LocalDateTime.of(crearCitaNoAutenticadaDTO.fecha(),
                    crearCitaNoAutenticadaDTO.hora());
            if (fechaHoraCita.isBefore(LocalDateTime.now())) {
                throw new RuntimeException("No se pueden crear citas en fechas pasadas");
            }

            // Validar disponibilidad del doctor
            if (!serviciosDisponibilidadDoctor.validarDisponibilidadDoctor(
                    crearCitaNoAutenticadaDTO.doctorId(),
                    crearCitaNoAutenticadaDTO.fecha(),
                    crearCitaNoAutenticadaDTO.hora())) {
                throw new RuntimeException("El doctor no está disponible en ese horario");
            }

            // Validar que no exista otra cita en el mismo horario
            if (citasRepository.existsByDoctorAndFechaHora(doctor,
                    fechaHoraCita.atZone(ZoneId.systemDefault()).toInstant())) {
                throw new RuntimeException("Ya existe una cita programada para ese horario");
            }

            // Obtener el tipo de cita
            TipoCita tipoCita = tipoCitaRepository.findById(crearCitaNoAutenticadaDTO.tipoCitaId())
                    .orElseThrow(() -> new RuntimeException("Tipo de cita no encontrado"));

            // Crear la cita no autenticada
            Cita cita = new Cita(
                    crearCitaNoAutenticadaDTO.nombrePaciente(),
                    crearCitaNoAutenticadaDTO.pacienteId(),
                    crearCitaNoAutenticadaDTO.telefono(),
                    crearCitaNoAutenticadaDTO.email(),
                    doctor,
                    fechaHoraCita.atZone(ZoneId.systemDefault()).toInstant(),
                    EstadoCitas.PENDIENTE,
                    tipoCita);

            // Guardar la cita
            Cita citaGuardada = citasRepository.save(cita);
            System.out.println("Cita no autenticada creada exitosamente con ID: " + citaGuardada.getId());

            // Enviar correo de confirmación
            enviarCorreoConfirmacionCita(citaGuardada);

            return citaGuardada;
        } catch (Exception e) {
            logger.error("Error al crear la cita no autenticada", e);
            throw new RuntimeException("Error al crear la cita no autenticada: " + e.getMessage());
        }
    }

    /**
     * Permite al administrador editar los datos de una cita no autenticada.
     * Puede modificar todos los datos del paciente y la cita.
     */
    @Override
    @Transactional
    public Cita editarCitaNoAutenticadaAdmin(Long idCita, EditarCitaNoAutenticadaAdminDTO dto) {
        System.out.println("\n=== Editando cita no autenticada ID: " + idCita + " (Admin) ===");
        try {
            Cita cita = citasRepository.findById(idCita)
                    .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));

            // Validar que la cita sea no autenticada
            if (cita.isEsAutenticada()) {
                throw new IllegalArgumentException(
                        "Esta cita es autenticada, use el método de edición para citas autenticadas");
            }

            // Validar que el doctor exista
            User doctor = userRepository.findByIdNumber(dto.doctorId())
                    .orElseThrow(() -> new IllegalArgumentException("Doctor no encontrado"));

            // Validar que el usuario sea un doctor
            if (!doctor.getAccount().getRol().equals(Rol.DOCTOR)) {
                throw new IllegalArgumentException("El usuario especificado no es un doctor");
            }

            // Validar que la fecha no sea en el pasado
            LocalDateTime fechaHoraCita = LocalDateTime.of(dto.fecha(), dto.hora());
            if (fechaHoraCita.isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("La fecha de la cita no puede ser en el pasado");
            }

            // Obtener el tipo de cita
            TipoCita tipoCita = tipoCitaRepository.findById(dto.tipoCitaId())
                    .orElseThrow(() -> new IllegalArgumentException("Tipo de cita no encontrado"));

            // Actualizar la cita
            cita.setNombrePacienteNoAutenticado(dto.nombrePaciente());
            cita.setNumeroIdentificacionNoAutenticado(dto.numeroIdentificacion());
            cita.setTelefonoNoAutenticado(dto.telefono());
            cita.setEmailNoAutenticado(dto.email());
            cita.setDoctor(doctor);
            cita.setFechaHora(fechaHoraCita.atZone(ZoneId.systemDefault()).toInstant());
            cita.setTipoCita(tipoCita);

            cita = citasRepository.save(cita);
            System.out.println("Cita no autenticada actualizada exitosamente");

            // Enviar correo de actualización
            try {
                enviarCorreoConfirmacionCita(cita);
            } catch (Exception e) {
                logger.warn("No se pudo enviar el correo de actualización: {}", e.getMessage());
            }

            return cita;
        } catch (IllegalArgumentException e) {
            logger.warn("Error de validación al editar cita no autenticada: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error inesperado al editar cita no autenticada", e);
            throw new RuntimeException("Error al editar la cita. Por favor, intente nuevamente.");
        }
    }

    /**
     * Permite al administrador cancelar una cita no autenticada.
     * Envía notificación por correo electrónico.
     */
    @Override
    @Transactional
    public void cancelarCitaNoAutenticadaAdmin(Long idCita) {
        System.out.println("\n=== Cancelando cita no autenticada ID: " + idCita + " (Admin) ===");
        try {
            Cita cita = citasRepository.findById(idCita)
                    .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));

            // Validar que la cita sea no autenticada
            if (cita.isEsAutenticada()) {
                throw new IllegalArgumentException(
                        "Esta cita es autenticada, use el método de cancelación para citas autenticadas");
            }

            // Validar que la cita no esté ya cancelada o completada
            if (cita.getEstado() == EstadoCitas.CANCELADA) {
                throw new IllegalArgumentException("La cita ya está cancelada");
            }
            if (cita.getEstado() == EstadoCitas.COMPLETADA) {
                throw new IllegalArgumentException("No se puede cancelar una cita completada");
            }

            // Cancelar la cita
            cita.setEstado(EstadoCitas.CANCELADA);
            citasRepository.save(cita);
            System.out.println("Cita no autenticada cancelada exitosamente");

            // Enviar correo de cancelación
            try {
                LocalDateTime fechaHoraLocal = cita.getFechaHora().atZone(ZoneId.systemDefault()).toLocalDateTime();
                emailService.enviarCorreoCancelacionCita(
                        cita.getEmailNoAutenticado(),
                        cita.getDoctor().getName() + " " + cita.getDoctor().getLastName(),
                        fechaHoraLocal);
            } catch (Exception e) {
                logger.warn("No se pudo enviar el correo de cancelación: {}", e.getMessage());
            }
        } catch (IllegalArgumentException e) {
            logger.warn("Error de validación al cancelar cita no autenticada: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error inesperado al cancelar cita no autenticada", e);
            throw new RuntimeException("Error al cancelar la cita. Por favor, intente nuevamente.");
        }
    }

    /**
     * Permite al administrador cambiar el estado de una cita no autenticada.
     * Maneja las transiciones de estado y envía notificaciones correspondientes.
     */
    @Override
    @Transactional
    public void cambiarEstadoCitaNoAutenticadaAdmin(Long idCita, EstadoCitas nuevoEstado) {
        System.out.println(
                "\n=== Cambiando estado de cita no autenticada ID: " + idCita + " a " + nuevoEstado + " (Admin) ===");
        try {
            Cita cita = citasRepository.findById(idCita)
                    .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));

            // Validar que la cita sea no autenticada
            if (cita.isEsAutenticada()) {
                throw new IllegalArgumentException(
                        "Esta cita es autenticada, use el método de cambio de estado para citas autenticadas");
            }

            // Validar el nuevo estado
            if (nuevoEstado == null) {
                throw new IllegalArgumentException("El nuevo estado no puede ser nulo");
            }

            // Validar transiciones de estado válidas
            if (cita.getEstado() == EstadoCitas.CANCELADA && nuevoEstado != EstadoCitas.CANCELADA) {
                throw new IllegalArgumentException("No se puede cambiar el estado de una cita cancelada");
            }
            if (cita.getEstado() == EstadoCitas.COMPLETADA && nuevoEstado != EstadoCitas.COMPLETADA) {
                throw new IllegalArgumentException("No se puede cambiar el estado de una cita completada");
            }

            // Cambiar el estado
            cita.setEstado(nuevoEstado);
            citasRepository.save(cita);
            System.out.println("Estado de cita no autenticada cambiado exitosamente a " + nuevoEstado);

            // Enviar notificación según el nuevo estado
            try {
                LocalDateTime fechaHoraLocal = cita.getFechaHora().atZone(ZoneId.systemDefault()).toLocalDateTime();
                switch (nuevoEstado) {
                    case CONFIRMADA:
                        emailService.enviarCorreoConfirmacionCita(
                                cita.getEmailNoAutenticado(),
                                cita.getDoctor().getName() + " " + cita.getDoctor().getLastName(),
                                fechaHoraLocal);
                        break;
                    case CANCELADA:
                        emailService.enviarCorreoCancelacionCita(
                                cita.getEmailNoAutenticado(),
                                cita.getDoctor().getName() + " " + cita.getDoctor().getLastName(),
                                fechaHoraLocal);
                        break;
                    case COMPLETADA:
                        emailService.enviarCorreoCitaCompletada(
                                cita.getEmailNoAutenticado(),
                                cita.getDoctor().getName() + " " + cita.getDoctor().getLastName(),
                                fechaHoraLocal);
                        break;
                }
            } catch (Exception e) {
                logger.warn("No se pudo enviar la notificación de cambio de estado: {}", e.getMessage());
            }
        } catch (IllegalArgumentException e) {
            logger.warn("Error de validación al cambiar estado de cita no autenticada: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error inesperado al cambiar estado de cita no autenticada", e);
            throw new RuntimeException("Error al cambiar el estado de la cita. Por favor, intente nuevamente.");
        }
    }

    // ==============================================
    // MÉTODOS DE CONSULTA Y DISPONIBILIDAD
    // ==============================================

    /**
     * Obtiene los doctores disponibles para una especialidad específica.
     * Incluye información de disponibilidad horaria.
     */
    @Override
    public List<DoctorEspecialidadDTO> obtenerDoctoresPorEspecialidad(Long especialidadId) {
        System.out.println("\n=== Obteniendo doctores para la especialidad ID: " + especialidadId + " ===");
        try {
            // 1. Verificar que la especialidad existe
            Especialidad especialidad = especialidadRepository.findById(especialidadId)
                    .orElseThrow(
                            () -> new IllegalArgumentException("Especialidad no encontrada con ID: " + especialidadId));

            System.out.println("Especialidad encontrada: " + especialidad.getNombre());

            // 2. Obtener todos los doctores
            List<User> doctores = userRepository.findByAccount_Rol(Rol.DOCTOR);
            System.out.println("Total de doctores en el sistema: " + doctores.size());

            // 3. Filtrar doctores que tienen la especialidad requerida
            List<User> doctoresFiltrados = doctores.stream()
                    .filter(doctor -> doctor.getEspecialidades() != null &&
                            doctor.getEspecialidades().contains(especialidad))
                    .collect(Collectors.toList());

            System.out.println("Doctores encontrados para la especialidad " + especialidad.getNombre() + ": "
                    + doctoresFiltrados.size());

            // 4. Convertir a DTOs
            List<DoctorEspecialidadDTO> doctoresDTO = doctoresFiltrados.stream()
                    .map(doctor -> {
                        // Obtener disponibilidad del doctor
                        List<DisponibilidadDTO> disponibilidadDTO = new ArrayList<>();

                        // Obtener disponibilidad para cada día de la semana
                        for (DayOfWeek dia : DayOfWeek.values()) {
                            List<DisponibilidadDoctor> disponibilidadesDia = disponibilidadDoctorRepository
                                    .findByDoctor_IdNumberAndDiaSemanaAndEstado(doctor.getIdNumber(), dia,
                                            EstadoDisponibilidad.ACTIVO);

                            // Convertir a DTOs
                            disponibilidadesDia.forEach(d -> disponibilidadDTO
                                    .add(new DisponibilidadDTO(d.getDiaSemana(), d.getHoraInicio(), d.getHoraFin())));
                        }

                        // Crear DTO del doctor
                        return new DoctorEspecialidadDTO(
                                doctor.getIdNumber(),
                                doctor.getName(),
                                doctor.getLastName(),
                                especialidad.getNombre(),
                                disponibilidadDTO);
                    })
                    .collect(Collectors.toList());

            doctoresDTO.forEach(doctor -> System.out
                    .println("- " + doctor.nombre() + " " + doctor.apellido() + " (ID: " + doctor.id() + ")"));

            return doctoresDTO;
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al obtener los doctores. Por favor, intente nuevamente.");
        }
    }

    /**
     * Obtiene las fechas disponibles para un doctor en un rango específico.
     * Considera las citas existentes y la disponibilidad del doctor.
     */
    @Override
    public List<FechaDisponibleDTO> obtenerFechasDisponibles(String doctorId, LocalDate fechaInicio,
            LocalDate fechaFin) {
        System.out.println("\n=== Obteniendo fechas disponibles para el doctor ID: " + doctorId + " ===");
        System.out.println("Rango de fechas: " + fechaInicio + " a " + fechaFin);

        try {
            // 1. Verificar que el doctor existe
            User doctor = userRepository.findByIdNumber(doctorId)
                    .orElseThrow(() -> new IllegalArgumentException("Doctor no encontrado con ID: " + doctorId));

            System.out.println("Doctor encontrado: " + doctor.getName() + " " + doctor.getLastName());

            // 2. Obtener las disponibilidades del doctor
            List<DisponibilidadDoctor> disponibilidades = new ArrayList<>();

            try {
                // Obtener disponibilidades para cada día de la semana
                for (DayOfWeek dia : DayOfWeek.values()) {
                    System.out.println("Buscando disponibilidades para el día: " + dia);
                    List<DisponibilidadDoctor> disponibilidadesDia = disponibilidadDoctorRepository
                            .findByDoctor_IdNumberAndDiaSemanaAndEstado(doctorId, dia, EstadoDisponibilidad.ACTIVO);
                    System.out.println("Disponibilidades encontradas para " + dia + ": " + disponibilidadesDia.size());
                    disponibilidades.addAll(disponibilidadesDia);
                }
            } catch (Exception e) {
                System.out.println("Error al buscar disponibilidades: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Error al buscar disponibilidades: " + e.getMessage());
            }

            if (disponibilidades.isEmpty()) {
                System.out.println("El doctor no tiene disponibilidades registradas");
                return new ArrayList<>();
            }

            // 3. Generar lista de fechas disponibles con horarios
            List<FechaDisponibleDTO> fechasDisponibles = new ArrayList<>();
            LocalDate fechaActual = fechaInicio;

            while (!fechaActual.isAfter(fechaFin)) {
                DayOfWeek diaSemana = fechaActual.getDayOfWeek();

                // Verificar si el doctor tiene disponibilidad para este día de la semana
                List<DisponibilidadDoctor> disponibilidadesDia = disponibilidades.stream()
                        .filter(d -> d.getDiaSemana() == diaSemana)
                        .collect(Collectors.toList());

                if (!disponibilidadesDia.isEmpty()) {
                    // Generar horarios disponibles para este día
                    List<HorarioDisponibleDTO> horariosDisponibles = new ArrayList<>();

                    for (DisponibilidadDoctor disponibilidad : disponibilidadesDia) {
                        LocalTime horaActual = disponibilidad.getHoraInicio();
                        LocalTime horaFin = disponibilidad.getHoraFin();

                        while (horaActual.isBefore(horaFin)) {
                            // Verificar si ya existe una cita en este horario
                            boolean existeCita = citasRepository.existsByDoctorAndFechaHoraBetween(
                                    doctor,
                                    fechaActual.atTime(horaActual).atZone(ZoneId.systemDefault()).toInstant(),
                                    fechaActual.atTime(horaActual.plusMinutes(30)).atZone(ZoneId.systemDefault())
                                            .toInstant());

                            horariosDisponibles.add(new HorarioDisponibleDTO(horaActual, !existeCita));
                            horaActual = horaActual.plusMinutes(30); // Intervalos de 30 minutos
                        }
                    }

                    // Agregar fecha con horarios disponibles
                    fechasDisponibles.add(new FechaDisponibleDTO(fechaActual, horariosDisponibles));
                }

                fechaActual = fechaActual.plusDays(1);
            }

            System.out.println("Se encontraron " + fechasDisponibles.size() + " fechas disponibles");
            fechasDisponibles.forEach(fecha -> {
                System.out.println("- " + fecha.fecha() + " con " + fecha.horarios().size() + " horarios");
                fecha.horarios().stream()
                        .filter(h -> h.disponible())
                        .forEach(hora -> System.out.println("  * " + hora.hora()));
            });

            return fechasDisponibles;
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al obtener las fechas disponibles: " + e.getMessage());
        }
    }

    // ==============================================
    // MÉTODOS PRIVADOS DE APOYO
    // ==============================================

    /**
     * Envía correo de confirmación de cita.
     * Maneja tanto citas autenticadas como no autenticadas.
     */
    private void enviarCorreoConfirmacionCita(Cita cita) {
        try {
            LocalDateTime fechaHoraLocal = cita.getFechaHora().atZone(ZoneId.systemDefault()).toLocalDateTime();
            CitaEmailDTO emailDTO;

            if (cita.isEsAutenticada()) {
                // Para citas autenticadas
                emailDTO = new CitaEmailDTO(
                        cita.getPaciente().getAccount().getEmail(),
                        cita.getPaciente().getName() + " " + cita.getPaciente().getLastName(),
                        cita.getDoctor().getName() + " " + cita.getDoctor().getLastName(),
                        fechaHoraLocal.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                        cita.getTipoCita().getNombre());
            } else {
                // Para citas no autenticadas
                emailDTO = new CitaEmailDTO(
                        cita.getEmailNoAutenticado(),
                        cita.getNombrePacienteNoAutenticado(),
                        cita.getDoctor().getName() + " " + cita.getDoctor().getLastName(),
                        fechaHoraLocal.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                        cita.getTipoCita().getNombre());
            }

            emailService.enviarCorreoCita(emailDTO);
        } catch (Exception e) {
            logger.warn("No se pudo enviar el correo de confirmación: {}", e.getMessage());
        }
    }
}