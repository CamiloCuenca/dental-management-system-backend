package edu.uniquindio.dentalmanagementsystembackend.service.impl;

import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoCitas;
import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoDisponibilidad;
import edu.uniquindio.dentalmanagementsystembackend.Enum.Rol;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.CrearCitaDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.DisponibilidadDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.DoctorEspecialidadDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.EditarCitaAdminDTO;
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
            LocalDateTime fechaHoraCita = crearCitaDTO.getFechaHora();
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
            if (citasRepository.existsByDoctorAndFechaHora(doctor, fechaHoraCita.atZone(ZoneId.systemDefault()).toInstant())) {
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
            cita.setEstado(EstadoCitas.PENDIENTE);
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

    @Override
    public List<DoctorEspecialidadDTO> obtenerDoctoresPorEspecialidad(Long especialidadId) {
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

            // 4. Convertir a DTOs
            List<DoctorEspecialidadDTO> doctoresDTO = doctoresFiltrados.stream()
                    .map(doctor -> {
                        // Obtener disponibilidad del doctor
                        List<DisponibilidadDTO> disponibilidadDTO = new ArrayList<>();

                        // Obtener disponibilidad para cada día de la semana
                        for (DayOfWeek dia : DayOfWeek.values()) {
                            List<DisponibilidadDoctor> disponibilidadesDia = disponibilidadDoctorRepository
                                    .findByDoctor_IdNumberAndDiaSemanaAndEstado(doctor.getIdNumber(), dia, EstadoDisponibilidad.ACTIVO);

                            // Convertir a DTOs
                            disponibilidadesDia.forEach(d ->
                                    disponibilidadDTO.add(new DisponibilidadDTO(d.getDiaSemana(), d.getHoraInicio(), d.getHoraFin()))
                            );
                        }

                        // Crear DTO del doctor
                        return new DoctorEspecialidadDTO(
                                doctor.getIdNumber(),
                                doctor.getName(),
                                doctor.getLastName(),
                                especialidad.getNombre(),
                                disponibilidadDTO
                        );
                    })
                    .collect(Collectors.toList());

            doctoresDTO.forEach(doctor ->
                    System.out.println("- " + doctor.nombre() + " " + doctor.apellido() + " (ID: " + doctor.id() + ")")
            );

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

    @Override
    public List<CitaDTO> obtenerCitasPorPaciente(String idPaciente) {
        System.out.println("\n=== Obteniendo citas para el paciente ID: " + idPaciente + " ===");
        try {
            List<Cita> citas = citasRepository.findByPaciente_IdNumber(idPaciente);
            System.out.println("Se encontraron " + citas.size() + " citas para el paciente");

            return citas.stream()
                    .map(cita -> new CitaDTO(
                            cita.getId(),
                            cita.getPaciente().getIdNumber(),
                            cita.getPaciente().getName() + " " + cita.getPaciente().getLastName(),
                            cita.getDoctor().getIdNumber(),
                            cita.getDoctor().getName() + " " + cita.getDoctor().getLastName(),
                            cita.getFechaHora(),
                            cita.getEstado(),
                            cita.getTipoCita().getId(),
                            cita.getTipoCita().getNombre(),
                            cita.getTipoCita().getDuracionMinutos()
                    ))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error al obtener las citas del paciente", e);
            throw new RuntimeException("Error al obtener las citas. Por favor, intente nuevamente.");
        }
    }

    @Override
    public List<CitaDTO> obtenerCitasPorDoctor(String idDoctor) {
        System.out.println("\n=== Obteniendo citas para el doctor ID: " + idDoctor + " ===");
        try {
            List<Cita> citas = citasRepository.findByDoctor_IdNumber(idDoctor);
            System.out.println("Se encontraron " + citas.size() + " citas para el doctor");

            return citas.stream()
                    .map(cita -> new CitaDTO(
                            cita.getId(),
                            cita.getPaciente().getIdNumber(),
                            cita.getPaciente().getName() + " " + cita.getPaciente().getLastName(),
                            cita.getDoctor().getIdNumber(),
                            cita.getDoctor().getName() + " " + cita.getDoctor().getLastName(),
                            cita.getFechaHora(),
                            cita.getEstado(),
                            cita.getTipoCita().getId(),
                            cita.getTipoCita().getNombre(),
                            cita.getTipoCita().getDuracionMinutos()
                    ))
                    .collect(Collectors.toList());
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

    @Override
    public List<FechaDisponibleDTO> obtenerFechasDisponibles(String doctorId, LocalDate fechaInicio, LocalDate fechaFin) {
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
                                    fechaActual.atTime(horaActual.plusMinutes(30)).atZone(ZoneId.systemDefault()).toInstant()
                            );

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

    private void enviarCorreoConfirmacionCita(Cita cita) {
        try {
            LocalDateTime fechaHoraLocal = cita.getFechaHora().atZone(ZoneId.systemDefault()).toLocalDateTime();
            CitaEmailDTO emailDTO = new CitaEmailDTO(
                    cita.getPaciente().getAccount().getEmail(),
                    cita.getPaciente().getName() + " " + cita.getPaciente().getLastName(),
                    cita.getDoctor().getName() + " " + cita.getDoctor().getLastName(),
                    cita.getTipoCita().getNombre(),
                    fechaHoraLocal.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
            );
            emailService.enviarCorreoCita(emailDTO);
        } catch (Exception e) {
            logger.warn("No se pudo enviar el correo de confirmación: {}", e.getMessage());
        }
    }
}