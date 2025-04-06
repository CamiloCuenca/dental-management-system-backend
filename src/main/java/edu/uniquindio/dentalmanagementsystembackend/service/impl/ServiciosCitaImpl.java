package edu.uniquindio.dentalmanagementsystembackend.service.impl;

import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoCitas;
import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoDisponibilidad;
import edu.uniquindio.dentalmanagementsystembackend.Enum.Rol;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.CrearCitaDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.EditarCitaAdminDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.EditarCitaPacienteDTO;
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

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Cita crearCita(CrearCitaDTO dto) {
        System.out.println("\n=== Creando nueva cita ===");
        System.out.println("Paciente ID: " + dto.pacienteId());
        System.out.println("Doctor ID: " + dto.odontologoId());
        System.out.println("Tipo de cita ID: " + dto.tipoCitaId());
        System.out.println("Fecha y hora: " + dto.fechaHora());
        
        try {
            // 1. Validar que el paciente y doctor existan
            User paciente = userRepository.findByIdNumber(dto.pacienteId())
                    .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado"));

            User doctor = userRepository.findByIdNumber(dto.odontologoId())
                    .orElseThrow(() -> new IllegalArgumentException("Doctor no encontrado"));

            // 2. Validar que el usuario sea un doctor
            if (!doctor.getAccount().getRol().equals(Rol.DOCTOR)) {
                throw new IllegalArgumentException("El usuario no es un doctor");
            }

            // 3. Validar que el tipo de cita exista
            TipoCita tipoCita = tipoCitaRepository.findById(dto.tipoCitaId())
                    .orElseThrow(() -> new IllegalArgumentException("Tipo de cita no encontrado"));

            // 4. Validar que la fecha no sea en el pasado
            if (dto.fechaHora().isBefore(Instant.now())) {
                throw new IllegalArgumentException("La fecha de la cita no puede ser en el pasado");
            }

            // 5. Validar que el doctor tenga al menos una especialidad (solo advertencia)
            if (doctor.getEspecialidades() == null || doctor.getEspecialidades().isEmpty()) {
                logger.warn("El doctor {} no tiene especialidades registradas", doctor.getIdNumber());
            }

            // 6. Validar disponibilidad del doctor
            LocalDateTime fechaHoraLocal = dto.fechaHora().atZone(ZoneId.systemDefault()).toLocalDateTime();
            DayOfWeek diaSemana = fechaHoraLocal.getDayOfWeek();
            LocalTime horaCita = fechaHoraLocal.toLocalTime();

            // Verificar si el doctor tiene disponibilidad para ese día y hora
            List<DisponibilidadDoctor> disponibilidades = disponibilidadDoctorRepository
                    .findByDoctor_IdNumberAndDiaSemanaAndEstado(dto.odontologoId(), diaSemana, EstadoDisponibilidad.ACTIVO);

            if (disponibilidades.isEmpty()) {
                throw new IllegalArgumentException("El doctor no tiene disponibilidad para el día seleccionado");
            }

            // Verificar si la hora de la cita está dentro del horario de disponibilidad
            boolean horaValida = disponibilidades.stream()
                    .anyMatch(d -> !horaCita.isBefore(d.getHoraInicio()) && horaCita.isBefore(d.getHoraFin()));

            if (!horaValida) {
                throw new IllegalArgumentException("La hora seleccionada no está dentro del horario de disponibilidad del doctor");
            }

            // Verificar si ya existe una cita en ese horario
            boolean existeCita = citasRepository.existsByDoctorAndFechaHoraBetween(
                    doctor,
                    dto.fechaHora(),
                    dto.fechaHora().plusSeconds(tipoCita.getDuracionMinutos() * 60)
            );

            if (existeCita) {
                throw new IllegalArgumentException("Ya existe una cita programada en ese horario");
            }

            // 7. Crear y guardar la cita
            Cita cita = new Cita();
            cita.setPaciente(paciente);
            cita.setDoctor(doctor);
            cita.setFechaHora(dto.fechaHora());
            cita.setEstado(EstadoCitas.PENDIENTE);
            cita.setTipoCita(tipoCita);

            cita = citasRepository.save(cita);
            
            System.out.println("Cita creada exitosamente con ID: " + cita.getId());
            
            // 8. Enviar correo de confirmación (opcional)
            try {
                CitaEmailDTO emailDTO = new CitaEmailDTO(
                        paciente.getAccount().getEmail(),
                        doctor.getName() + " " + doctor.getLastName(),
                        paciente.getName() + " " + paciente.getLastName(),
                        tipoCita.getNombre(),
                        fechaHoraLocal.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                );
                emailService.enviarCorreoCita(emailDTO);
            } catch (Exception e) {
                logger.warn("No se pudo enviar el correo de confirmación: {}", e.getMessage());
            }

            return cita;
        } catch (IllegalArgumentException e) {
            logger.warn("Error de validación al crear cita: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error inesperado al crear cita", e);
            throw new RuntimeException("Error al crear la cita. Por favor, intente nuevamente.");
        }
    }

    @Override
    public List<User> obtenerDoctoresPorEspecialidad(Long especialidadId) {
        System.out.println("\n=== Obteniendo doctores para la especialidad ID: " + especialidadId + " ===");
        try {
            // 1. Verificar que la especialidad existe
            Especialidad especialidad = especialidadRepository.findById(especialidadId)
                    .orElseThrow(() -> new IllegalArgumentException("Especialidad no encontrada con ID: " + especialidadId));
            
            System.out.println("Especialidad encontrada: " + especialidad.getNombre());
            
            // 2. Obtener todos los doctores
            List<User> doctores = userRepository.findByAccount_Rol(Rol.DOCTOR);
            System.out.println("Total de doctores en el sistema: " + doctores.size());
            
            // 3. Filtrar doctores que tienen la especialidad requerida
            List<User> doctoresFiltrados = doctores.stream()
                    .filter(doctor -> doctor.getEspecialidades() != null && 
                                     doctor.getEspecialidades().contains(especialidad))
                    .collect(Collectors.toList());
            
            System.out.println("Doctores encontrados para la especialidad " + especialidad.getNombre() + ": " + doctoresFiltrados.size());
            doctoresFiltrados.forEach(doctor -> 
                System.out.println("- " + doctor.getName() + " " + doctor.getLastName() + " (ID: " + doctor.getIdNumber() + ")")
            );
            
            return doctoresFiltrados;
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al obtener los doctores. Por favor, intente nuevamente.");
        }
    }

    @Override
    public List<Cita> obtenerCitasPorPaciente(String idPaciente) {
        System.out.println("\n=== Obteniendo citas para el paciente ID: " + idPaciente + " ===");
        try {
            List<Cita> citas = citasRepository.findByPaciente_IdNumber(idPaciente);
            System.out.println("Se encontraron " + citas.size() + " citas para el paciente");
            return citas;
        } catch (Exception e) {
            logger.error("Error al obtener las citas del paciente", e);
            throw new RuntimeException("Error al obtener las citas. Por favor, intente nuevamente.");
        }
    }

    @Override
    public List<Cita> obtenerCitasPorDoctor(String idDoctor) {
        System.out.println("\n=== Obteniendo citas para el doctor ID: " + idDoctor + " ===");
        try {
            List<Cita> citas = citasRepository.findByDoctor_IdNumber(idDoctor);
            System.out.println("Se encontraron " + citas.size() + " citas para el doctor");
            return citas;
        } catch (Exception e) {
            logger.error("Error al obtener las citas del doctor", e);
            throw new RuntimeException("Error al obtener las citas. Por favor, intente nuevamente.");
        }
    }

    @Override
    @Transactional
    public Cita editarCitaAdmin(Long idCita, EditarCitaAdminDTO dto) {
        System.out.println("\n=== Editando cita ID: " + idCita + " (Admin) ===");
        try {
            Cita cita = citasRepository.findById(idCita)
                    .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));

            // Validar que el paciente y doctor existan
            User paciente = userRepository.findByIdNumber(dto.pacienteId().toString())
                    .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado"));

            User doctor = userRepository.findByIdNumber(dto.odontologoId().toString())
                    .orElseThrow(() -> new IllegalArgumentException("Doctor no encontrado"));

            // Validar que el usuario sea un doctor
            if (!doctor.getAccount().getRol().equals(Rol.DOCTOR)) {
                throw new IllegalArgumentException("El usuario no es un doctor");
            }

            // Validar que la fecha no sea en el pasado
            if (dto.fechaHora().isBefore(Instant.now())) {
                throw new IllegalArgumentException("La fecha de la cita no puede ser en el pasado");
            }

            // Actualizar la cita
            cita.setPaciente(paciente);
            cita.setDoctor(doctor);
            cita.setFechaHora(dto.fechaHora());

            cita = citasRepository.save(cita);
            System.out.println("Cita actualizada exitosamente");

            return cita;
        } catch (IllegalArgumentException e) {
            logger.warn("Error de validación al editar cita: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error inesperado al editar cita", e);
            throw new RuntimeException("Error al editar la cita. Por favor, intente nuevamente.");
        }
    }

    @Override
    @Transactional
    public Cita editarCitaPaciente(Long idCita, EditarCitaPacienteDTO dto) {
        System.out.println("\n=== Editando cita ID: " + idCita + " (Paciente) ===");
        try {
            Cita cita = citasRepository.findById(idCita)
                    .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));

            // Validar que la fecha no sea en el pasado
            if (dto.fechaHora().isBefore(Instant.now())) {
                throw new IllegalArgumentException("La fecha de la cita no puede ser en el pasado");
            }

            // Validar que la cita no esté cancelada o completada
            if (cita.getEstado() == EstadoCitas.CANCELADA || cita.getEstado() == EstadoCitas.COMPLETADA) {
                throw new IllegalArgumentException("No se puede editar una cita cancelada o completada");
            }

            // Actualizar la fecha de la cita
            cita.setFechaHora(dto.fechaHora());
            cita.setEstado(EstadoCitas.PENDIENTE); // La cita vuelve a estado pendiente al ser editada

            cita = citasRepository.save(cita);
            System.out.println("Cita actualizada exitosamente");

            return cita;
        } catch (IllegalArgumentException e) {
            logger.warn("Error de validación al editar cita: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error inesperado al editar cita", e);
            throw new RuntimeException("Error al editar la cita. Por favor, intente nuevamente.");
        }
    }

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
                        fechaHoraLocal
                );
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
                        fechaHoraLocal
                );
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
                        fechaHoraLocal
                );
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
}