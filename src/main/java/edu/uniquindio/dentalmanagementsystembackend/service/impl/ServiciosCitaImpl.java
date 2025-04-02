package edu.uniquindio.dentalmanagementsystembackend.service.impl;

import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoCitas;
import edu.uniquindio.dentalmanagementsystembackend.Enum.Rol;
import edu.uniquindio.dentalmanagementsystembackend.Enum.TipoCita;
import edu.uniquindio.dentalmanagementsystembackend.Enum.TipoDoctor;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.CitaDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.ListaCitasDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.DoctorDisponibilidadDTO;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.Account;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.User;
import edu.uniquindio.dentalmanagementsystembackend.entity.Cita;
import edu.uniquindio.dentalmanagementsystembackend.exception.CitaException;
import edu.uniquindio.dentalmanagementsystembackend.repository.CitasRepository;
import edu.uniquindio.dentalmanagementsystembackend.repository.CuentaRepository;
import edu.uniquindio.dentalmanagementsystembackend.repository.UserRepository;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.EmailService;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.ServiciosCitas;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de gestión de citas dentales.
 * Esta clase maneja toda la lógica de negocio relacionada con las citas,
 * incluyendo su creación, consulta, modificación y cancelación.
 */
@Transactional
@Service
public class ServiciosCitaImpl implements ServiciosCitas {
    //hola

    // Repositorio para operaciones CRUD de citas
    @Autowired
    private CitasRepository citasRepository;

    // Repositorio para operaciones relacionadas con cuentas de usuario
    @Autowired
    private CuentaRepository cuentaRepository;

    // Repositorio para operaciones CRUD de usuarios
    @Autowired
    private UserRepository userRepository;

    // Servicio para envío de correos electrónicos
    @Autowired
    private EmailService emailService;

    // ============= MÉTODOS DE CREACIÓN DE CITAS =============

    /**
     * Crea una nueva cita dental.
     * Este método realiza todas las validaciones necesarias antes de crear la cita.
     *
     * @param citaDTO Objeto DTO con los datos de la cita a crear
     * @throws Exception Si hay algún error en la creación o envío de notificación
     */
    @Override
    @Transactional
    public void crearCita(CitaDTO citaDTO) throws Exception {
        // Validar datos básicos de la cita
        validarDatosBasicos(citaDTO);
        
        // Validar que la fecha y hora sean válidas
        validarFechaCita(citaDTO.fechaHora());

        // Obtener y validar que el paciente exista y sea válido
        User paciente = obtenerYValidarPaciente(citaDTO.idPaciente());

        // Obtener y validar que el doctor exista y sea válido
        User doctor = obtenerYValidarDoctor(citaDTO.idDoctor());

        // Verificar que el paciente no tenga citas ese día
        validarDisponibilidadPaciente(paciente, citaDTO.fechaHora());

        // Verificar que el doctor esté disponible en ese horario
        if (!validarDisponibilidadDoctor(doctor, citaDTO.fechaHora())) {
            throw new CitaException("El doctor no está disponible en ese horario.");
        }

        // Crear y guardar la cita en la base de datos
        Instant instant = citaDTO.fechaHora().atZone(ZoneId.systemDefault()).toInstant();
        Cita cita = new Cita(paciente, doctor, instant, citaDTO.estado(), citaDTO.tipoCita());
        citasRepository.save(cita);

        // Enviar notificación por correo al paciente
        enviarNotificacionCita(paciente, doctor, citaDTO.fechaHora());
    }

    /**
     * Valida los datos básicos de una cita.
     * Verifica que todos los campos requeridos estén presentes y sean válidos.
     *
     * @param citaDTO Objeto DTO con los datos a validar
     * @throws CitaException Si algún dato no es válido
     */
    private void validarDatosBasicos(CitaDTO citaDTO) {
        if (citaDTO.idPaciente() == null || citaDTO.idPaciente() <= 0) {
            throw new CitaException("El ID del paciente no es válido.");
        }

        if (citaDTO.idDoctor() == null || citaDTO.idDoctor() <= 0) {
            throw new CitaException("El ID del doctor no es válido.");
        }

        if (citaDTO.fechaHora() == null) {
            throw new CitaException("La fecha y hora de la cita no pueden ser nulas.");
        }

        if (citaDTO.tipoCita() == null) {
            throw new CitaException("El tipo de cita no puede ser nulo.");
        }
        if (citaDTO.estado() == null) {
            throw new CitaException("El estado de la cita no puede ser nulo.");
        }
    }

    /**
     * Valida que la fecha y hora de la cita sean válidas.
     * Verifica que no sea en el pasado y que esté dentro del horario de atención.
     *
     * @param fechaHora Fecha y hora a validar
     * @throws CitaException Si la fecha no es válida
     */
    private void validarFechaCita(LocalDateTime fechaHora) {
        LocalDateTime ahora = LocalDateTime.now();
        if (fechaHora.isBefore(ahora)) {
            throw new CitaException("La fecha de la cita no puede ser en el pasado");
        }
        
        LocalTime horaInicio = LocalTime.of(8, 0);
        LocalTime horaFin = LocalTime.of(18, 0);
        if (fechaHora.toLocalTime().isBefore(horaInicio) || 
            fechaHora.toLocalTime().isAfter(horaFin)) {
            throw new CitaException("La cita debe estar dentro del horario de atención (8:00-18:00)");
        }
    }

    /**
     * Obtiene y valida que el paciente exista y tenga el rol correcto.
     *
     * @param idPaciente ID del paciente a validar
     * @return Objeto User del paciente
     * @throws CitaException Si el paciente no existe o no tiene el rol correcto
     */
    private User obtenerYValidarPaciente(Long idPaciente) {
        User paciente = userRepository.findById(idPaciente)
                .orElseThrow(() -> new CitaException("Paciente no encontrado"));

        Hibernate.initialize(paciente.getAccount());
        if (paciente.getAccount() == null || paciente.getAccount().getRol() != Rol.PACIENTE) {
            throw new CitaException("El usuario con ID " + idPaciente + " no es un paciente.");
        }

        return paciente;
    }

    /**
     * Obtiene y valida que el doctor exista y tenga el rol correcto.
     *
     * @param idDoctor ID del doctor a validar
     * @return Objeto User del doctor
     * @throws CitaException Si el doctor no existe o no tiene el rol correcto
     */
    private User obtenerYValidarDoctor(Long idDoctor) {
        User doctor = userRepository.findById(idDoctor)
                .orElseThrow(() -> new CitaException("Doctor no encontrado"));

        Hibernate.initialize(doctor.getAccount());
        if (doctor.getAccount() == null || doctor.getAccount().getRol() != Rol.DOCTOR) {
            throw new CitaException("El usuario con ID " + idDoctor + " no es un doctor.");
        }

        return doctor;
    }

    /**
     * Valida que el paciente no tenga citas programadas para el mismo día.
     *
     * @param paciente Paciente a validar
     * @param fechaHora Fecha y hora de la cita
     * @throws CitaException Si el paciente ya tiene una cita ese día
     */
    private void validarDisponibilidadPaciente(User paciente, LocalDateTime fechaHora) {
        LocalDate fechaCita = fechaHora.toLocalDate();
        Instant inicioDelDia = fechaCita.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant finDelDia = fechaCita.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant();

        boolean tieneCitaEseDia = citasRepository.findByPacienteAndFechaHoraBetween(paciente, inicioDelDia, finDelDia)
                .stream()
                .anyMatch(cita -> cita.getFechaHora().atZone(ZoneOffset.UTC).toLocalDate().equals(fechaCita));

        if (tieneCitaEseDia) {
            throw new CitaException("El paciente ya tiene una cita programada para ese día.");
        }
    }

    /**
     * Valida que el doctor esté disponible en el horario especificado.
     * Verifica que no tenga citas programadas 40 minutos antes y después.
     *
     * @param doctor Doctor a validar
     * @param fechaHora Fecha y hora de la cita
     * @return true si el doctor está disponible, false en caso contrario
     */
    private boolean validarDisponibilidadDoctor(User doctor, LocalDateTime fechaHora) {
        LocalDateTime inicioCita = fechaHora.minusMinutes(40);
        LocalDateTime finCita = fechaHora.plusMinutes(40);
        
        return citasRepository.findByOdontologoAndFechaHoraBetween(
            doctor,
            inicioCita.toInstant(ZoneOffset.UTC),
            finCita.toInstant(ZoneOffset.UTC)
        ).isEmpty();
    }

    /**
     * Envía una notificación por correo al paciente sobre su cita.
     *
     * @param paciente Paciente al que se enviará la notificación
     * @param doctor Doctor asignado a la cita
     * @param fechaHora Fecha y hora de la cita
     * @throws Exception Si hay algún error al enviar el correo
     */
    private void enviarNotificacionCita(User paciente, User doctor, LocalDateTime fechaHora) throws Exception {
        String emailPaciente = paciente.getAccount().getEmail();
        emailService.enviarCorreoConfirmacionCita(emailPaciente, doctor.getName(), fechaHora);
        System.out.println("✅ Cita creada correctamente con el doctor " + doctor.getName() + " en la fecha: " + fechaHora);
    }

    // ============= MÉTODOS DE CONSULTA DE CITAS =============

    /**
     * Obtiene todas las citas de un paciente específico.
     *
     * @param idPaciente ID del paciente
     * @return Lista de citas del paciente
     * @throws CitaException Si hay algún error en la consulta
     */
    @Override
    public List<ListaCitasDTO> obtenerCitasPorPaciente(Long idPaciente) {
        validarIdPaciente(idPaciente);
        List<Cita> citas = obtenerCitasPaciente(idPaciente);
        return mapearCitasADTO(citas);
    }

    /**
     * Valida que el ID del paciente sea válido y exista.
     *
     * @param idPaciente ID a validar
     * @throws CitaException Si el ID no es válido o el paciente no existe
     */
    private void validarIdPaciente(Long idPaciente) {
        if (idPaciente == null || idPaciente <= 0) {
            throw new CitaException("El ID del paciente no es válido.");
        }
        if (!userRepository.existsById(idPaciente)) {
            throw new CitaException("El paciente con ID " + idPaciente + " no existe.");
        }
    }

    /**
     * Obtiene las citas de un paciente desde la base de datos.
     *
     * @param idPaciente ID del paciente
     * @return Lista de citas del paciente
     * @throws CitaException Si el paciente no tiene citas
     */
    private List<Cita> obtenerCitasPaciente(Long idPaciente) {
        List<Cita> citas = citasRepository.findByPacienteId(String.valueOf(idPaciente));
        if (citas.isEmpty()) {
            throw new CitaException("El paciente con ID " + idPaciente + " no tiene citas registradas.");
        }
        return citas;
    }

    /**
     * Obtiene todas las citas del sistema.
     *
     * @return Lista de todas las citas
     * @throws CitaException Si no hay citas registradas
     */
    @Override
    public List<ListaCitasDTO> obtenerTodasLasCitas() {
        List<Cita> citas = citasRepository.findAll();
        if (citas.isEmpty()) {
            throw new CitaException("No hay citas registradas en el sistema.");
        }
        return mapearCitasADTO(citas);
    }

    /**
     * Mapea una lista de citas a DTOs.
     *
     * @param citas Lista de citas a mapear
     * @return Lista de DTOs de citas
     */
    private List<ListaCitasDTO> mapearCitasADTO(List<Cita> citas) {
        return citas.stream()
                .map(this::convertirCitaADTO)
                .collect(Collectors.toList());
    }

    /**
     * Convierte una cita a su representación DTO.
     *
     * @param cita Cita a convertir
     * @return DTO de la cita
     * @throws CitaException Si hay error en la conversión
     */
    private ListaCitasDTO convertirCitaADTO(Cita cita) {
        validarDatosCita(cita);
        try {
            return new ListaCitasDTO(
                    cita.getId(),
                    Long.parseLong(cita.getPaciente().getIdNumber()),
                    Long.parseLong(cita.getOdontologo().getIdNumber()),
                    cita.getFechaHora().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                    cita.getEstado(),
                    cita.getTipoCita()
            );
        } catch (NumberFormatException e) {
            throw new CitaException("Error al convertir el ID de la cita con ID " + cita.getId() + " a tipo Long.", e);
        }
    }

    /**
     * Valida que los datos de una cita sean válidos.
     *
     * @param cita Cita a validar
     * @throws CitaException Si algún dato no es válido
     */
    private void validarDatosCita(Cita cita) {
        if (cita.getPaciente() == null || cita.getOdontologo() == null || cita.getFechaHora() == null) {
            throw new CitaException("Error en los datos de la cita con ID " + cita.getId());
        }
    }

    // ============= MÉTODOS DE MODIFICACIÓN DE CITAS =============

    /**
     * Edita el tipo de una cita existente.
     *
     * @param idCita ID de la cita a editar
     * @param nuevoTipoCita Nuevo tipo de cita
     * @throws CitaException Si hay algún error en la edición
     */
    @Override
    @Transactional
    public void editarCita(Long idCita, TipoCita nuevoTipoCita) {
        validarDatosEdicion(idCita, nuevoTipoCita);
        Cita cita = obtenerYValidarCita(idCita);
        validarEstadoCita(cita);
        validarTiempoModificacion(cita);
        actualizarTipoCita(cita, nuevoTipoCita);
    }

    /**
     * Valida los datos necesarios para editar una cita.
     *
     * @param idCita ID de la cita
     * @param nuevoTipoCita Nuevo tipo de cita
     * @throws CitaException Si los datos no son válidos
     */
    private void validarDatosEdicion(Long idCita, TipoCita nuevoTipoCita) {
        if (idCita == null || idCita <= 0) {
            throw new CitaException("El ID de la cita no es válido.");
        }
        if (nuevoTipoCita == null) {
            throw new CitaException("El nuevo tipo de cita no puede ser nulo.");
        }
    }

    /**
     * Obtiene y valida que una cita exista.
     *
     * @param idCita ID de la cita
     * @return Cita encontrada
     * @throws CitaException Si la cita no existe
     */
    private Cita obtenerYValidarCita(Long idCita) {
        return citasRepository.findById(idCita)
                .orElseThrow(() -> new CitaException("Cita con ID " + idCita + " no encontrada."));
    }

    /**
     * Valida que el estado de la cita permita su edición.
     *
     * @param cita Cita a validar
     * @throws CitaException Si el estado no permite la edición
     */
    private void validarEstadoCita(Cita cita) {
        if (cita.getEstado() == EstadoCitas.CANCELADA || cita.getEstado() == EstadoCitas.COMPLETADA) {
            throw new CitaException("No se puede editar una cita que está cancelada o finalizada.");
        }
    }

    /**
     * Valida que haya suficiente tiempo para modificar la cita.
     *
     * @param cita Cita a validar
     * @throws CitaException Si no hay suficiente tiempo para modificar
     */
    private void validarTiempoModificacion(Cita cita) {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime fechaCita = cita.getFechaHora().atZone(ZoneId.systemDefault()).toLocalDateTime();
        if (Duration.between(ahora, fechaCita).toHours() < 24) {
            throw new CitaException("No se puede modificar la cita porque faltan menos de 24 horas para su inicio.");
        }
    }

    /**
     * Actualiza el tipo de una cita.
     *
     * @param cita Cita a actualizar
     * @param nuevoTipoCita Nuevo tipo de cita
     */
    private void actualizarTipoCita(Cita cita, TipoCita nuevoTipoCita) {
        cita.setTipoCita(nuevoTipoCita);
        citasRepository.save(cita);
        System.out.println("✅ Cita con ID " + cita.getId() + " actualizada correctamente a tipo: " + nuevoTipoCita);
    }

    /**
     * Cancela una cita existente.
     *
     * @param idCita ID de la cita a cancelar
     * @throws CitaException Si hay algún error en la cancelación
     */
    @Override
    @Transactional
    public void cancelarCita(Long idCita) {
        validarIdCita(idCita);
        Cita cita = obtenerYValidarCita(idCita);
        validarEstadoCancelacion(cita);
        realizarCancelacionCita(cita);
    }

    /**
     * Valida que el ID de la cita sea válido.
     *
     * @param idCita ID a validar
     * @throws CitaException Si el ID no es válido
     */
    private void validarIdCita(Long idCita) {
        if (idCita == null || idCita <= 0) {
            throw new CitaException("El ID de la cita no es válido.");
        }
    }

    /**
     * Valida que el estado de la cita permita su cancelación.
     *
     * @param cita Cita a validar
     * @throws CitaException Si el estado no permite la cancelación
     */
    private void validarEstadoCancelacion(Cita cita) {
        if (cita.getEstado() == EstadoCitas.CANCELADA) {
            throw new CitaException("La cita ya está cancelada.");
        }
        if (cita.getEstado() == EstadoCitas.COMPLETADA) {
            throw new CitaException("No se puede cancelar una cita que ya ha finalizado.");
        }
    }

    /**
     * Realiza la cancelación de una cita.
     *
     * @param cita Cita a cancelar
     */
    private void realizarCancelacionCita(Cita cita) {
        cita.setEstado(EstadoCitas.CANCELADA);
        citasRepository.save(cita);
        
        // Enviar notificación de cancelación
        String emailPaciente = cita.getPaciente().getAccount().getEmail();
        LocalDateTime fechaHora = cita.getFechaHora().atZone(ZoneId.systemDefault()).toLocalDateTime();
        emailService.enviarCorreoCancelacionCita(emailPaciente, cita.getOdontologo().getName(), fechaHora);
        
        System.out.println("✅ Cita con ID " + cita.getId() + " cancelada correctamente.");
    }

    // ============= MÉTODOS DE DISPONIBILIDAD =============

    /**
     * Obtiene las fechas disponibles para todos los doctores.
     *
     * @return Lista de disponibilidad de doctores
     */
    @Override
    public List<DoctorDisponibilidadDTO> obtenerFechasDisponiblesDoctores() {
        try {
            // Obtener solo los doctores activos
            List<Account> cuentasDoctores = cuentaRepository.findByRol(Rol.DOCTOR);

            return cuentasDoctores.stream()
                    .map(account -> {
                        User doctor = account.getUser();
                        List<LocalDateTime> fechasDisponibles = obtenerFechasDisponibles(doctor);
                        return new DoctorDisponibilidadDTO(
                                doctor.getIdNumber(),
                                doctor.getName() + " " + doctor.getLastName(),
                                account.getTipoDoctor() != null ? account.getTipoDoctor().name() : "OTRO",
                                fechasDisponibles
                        );
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new CitaException("Error al obtener las fechas disponibles de los doctores: " + e.getMessage());
        }
    }

    /**
     * Obtiene las fechas disponibles para un doctor específico.
     *
     * @param doctor Doctor para el que se obtienen las fechas
     * @return Lista de fechas disponibles
     */
    private List<LocalDateTime> obtenerFechasDisponibles(User doctor) {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime tresMesesAdelante = ahora.plusMonths(3);

        // 1. Obtener las fechas ocupadas
        Set<LocalDateTime> fechasOcupadas = obtenerFechasOcupadas(doctor, ahora, tresMesesAdelante);

        // 2. Generar todas las posibles fechas
        List<LocalDateTime> todasLasFechas = generarTodasLasFechas(ahora, tresMesesAdelante);

        // 3. Filtrar las fechas ocupadas
        return todasLasFechas.stream()
                .filter(fecha -> !fechasOcupadas.contains(fecha))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene las fechas ocupadas de un doctor en un rango de tiempo.
     *
     * @param doctor Doctor para el que se obtienen las fechas
     * @param inicio Fecha de inicio del rango
     * @param fin Fecha de fin del rango
     * @return Conjunto de fechas ocupadas
     */
    private Set<LocalDateTime> obtenerFechasOcupadas(User doctor, LocalDateTime inicio, LocalDateTime fin) {
        List<Cita> citas = citasRepository.findByOdontologoAndFechaHoraBetween(
            doctor, 
            inicio.toInstant(ZoneOffset.UTC), 
            fin.toInstant(ZoneOffset.UTC)
        );
        return citas.stream()
                .map(cita -> cita.getFechaHora().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .collect(Collectors.toSet());
    }

    /**
     * Genera todas las posibles fechas disponibles en un rango de tiempo.
     * Solo incluye días laborables (lunes a viernes) y horario de atención (8:00-18:00).
     *
     * @param inicio Fecha de inicio del rango
     * @param fin Fecha de fin del rango
     * @return Lista de fechas disponibles
     */
    private List<LocalDateTime> generarTodasLasFechas(LocalDateTime inicio, LocalDateTime fin) {
        List<LocalDateTime> fechas = new ArrayList<>();
        LocalTime horaInicio = LocalTime.of(8, 0);
        LocalTime horaFin = LocalTime.of(18, 0);

        for (LocalDateTime fecha = inicio; fecha.isBefore(fin); fecha = fecha.plusDays(1)) {
            // Solo incluir días laborables (lunes a viernes)
            if (fecha.getDayOfWeek().getValue() >= 1 && fecha.getDayOfWeek().getValue() <= 5) {
                for (LocalDateTime hora = fecha.with(horaInicio);
                     hora.isBefore(fecha.with(horaFin));
                     hora = hora.plusMinutes(40)) {
                    fechas.add(hora);
                }
            }
        }

        return fechas;
    }

    // ============= MÉTODOS DE CONFIRMACIÓN Y ESTADO =============

    @Override
    @Transactional
    public void confirmarCita(Long idCita) {
        validarIdCita(idCita);
        Cita cita = obtenerYValidarCita(idCita);
        validarEstadoConfirmacion(cita);
        cita.setEstado(EstadoCitas.CONFIRMADA);
        citasRepository.save(cita);
        enviarNotificacionConfirmacion(cita);
    }

    @Override
    @Transactional
    public void completarCita(Long idCita) {
        validarIdCita(idCita);
        Cita cita = obtenerYValidarCita(idCita);
        validarEstadoCompletar(cita);
        cita.setEstado(EstadoCitas.COMPLETADA);
        citasRepository.save(cita);
        enviarNotificacionCompletada(cita);
    }

    // ============= MÉTODOS DE BÚSQUEDA AVANZADA =============

    @Override
    public List<ListaCitasDTO> obtenerCitasPorFecha(LocalDate fecha) {
        Instant inicioDia = fecha.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant finDia = fecha.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant();
        List<Cita> citas = citasRepository.findByFechaHoraBetween(inicioDia, finDia);
        return mapearCitasADTO(citas);
    }

    @Override
    public List<ListaCitasDTO> obtenerCitasPorEstado(EstadoCitas estado) {
        List<Cita> citas = citasRepository.findByEstado(estado);
        return mapearCitasADTO(citas);
    }

    // ============= MÉTODOS DE REPROGRAMACIÓN =============

    @Override
    @Transactional
    public void reprogramarCita(Long idCita, LocalDateTime nuevaFechaHora) {
        validarIdCita(idCita);
        Cita cita = obtenerYValidarCita(idCita);
        validarFechaCita(nuevaFechaHora);
        validarDisponibilidadDoctor(cita.getOdontologo(), nuevaFechaHora);
        validarDisponibilidadPaciente(cita.getPaciente(), nuevaFechaHora);
        cita.setFechaHora(nuevaFechaHora.atZone(ZoneId.systemDefault()).toInstant());
        citasRepository.save(cita);
        enviarNotificacionReprogramacion(cita);
    }

    // ============= MÉTODOS DE ESTADÍSTICAS =============

    @Override
    public Map<EstadoCitas, Long> obtenerEstadisticasCitasPorEstado() {
        List<Cita> todasLasCitas = citasRepository.findAll();
        return todasLasCitas.stream()
                .collect(Collectors.groupingBy(
                    Cita::getEstado,
                    Collectors.counting()
                ));
    }

    @Override
    public Map<Long, Long> obtenerEstadisticasCitasPorDoctor() {
        List<Cita> todasLasCitas = citasRepository.findAll();
        return todasLasCitas.stream()
                .collect(Collectors.groupingBy(
                    cita -> Long.parseLong(cita.getOdontologo().getIdNumber()),
                    Collectors.counting()
                ));
    }

    // ============= MÉTODOS DE NOTIFICACIONES =============

    @Override
    public void enviarRecordatorioCita(Long idCita) {
        Cita cita = obtenerYValidarCita(idCita);
        if (cita.getEstado() != EstadoCitas.CONFIRMADA) {
            throw new CitaException("Solo se pueden enviar recordatorios de citas confirmadas");
        }
        enviarNotificacionRecordatorio(cita);
    }


    @Override
    public DoctorDisponibilidadDTO obtenerFechasDisponiblesDoctor(Long doctorId) {
        try {
            // 1. Obtener y validar el doctor
            User doctor = userRepository.findById(doctorId)
                    .orElseThrow(() -> new CitaException("Doctor no encontrado"));

            // 2. Obtener la cuenta del doctor para verificar el tipo
            Account cuentaDoctor = cuentaRepository.findByUser(doctor)
                    .orElseThrow(() -> new CitaException("Cuenta de doctor no encontrada"));

            // 3. Verificar que sea un doctor
            if (cuentaDoctor.getRol() != Rol.DOCTOR) {
                throw new CitaException("El usuario no es un doctor");
            }

            // 4. Obtener las fechas disponibles
            List<LocalDateTime> fechasDisponibles = obtenerFechasDisponibles(doctor);

            // 5. Crear y retornar el DTO
            return new DoctorDisponibilidadDTO(
                    doctor.getIdNumber(),
                    doctor.getName() + " " + doctor.getLastName(),
                    cuentaDoctor.getTipoDoctor() != null ? cuentaDoctor.getTipoDoctor().name() : "OTRO",
                    fechasDisponibles
            );
        } catch (Exception e) {
            throw new CitaException("Error al obtener las fechas disponibles del doctor: " + e.getMessage());
        }
    }

    @Override
    public List<DoctorDisponibilidadDTO> obtenerFechasDisponiblesPorTipoDoctor(String tipoDoctor) {
        try {
            // 1. Obtener todos los doctores del tipo especificado
            List<Account> cuentasDoctores = cuentaRepository.findByRolAndTipoDoctor(
                    Rol.DOCTOR,
                    TipoDoctor.valueOf(tipoDoctor)
            );

            // 2. Mapear cada doctor a su DTO con fechas disponibles
            return cuentasDoctores.stream()
                    .map(account -> {
                        User doctor = account.getUser();
                        List<LocalDateTime> fechasDisponibles = obtenerFechasDisponibles(doctor);
                        return new DoctorDisponibilidadDTO(
                                doctor.getIdNumber(),
                                doctor.getName() + " " + doctor.getLastName(),
                                account.getTipoDoctor().name(),
                                fechasDisponibles
                        );
                    })
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new CitaException("Tipo de doctor inválido: " + tipoDoctor);
        } catch (Exception e) {
            throw new CitaException("Error al obtener las fechas disponibles por tipo de doctor: " + e.getMessage());
        }
    }

    // ============= MÉTODOS PRIVADOS DE VALIDACIÓN =============

    private void validarEstadoConfirmacion(Cita cita) {
        if (cita.getEstado() != EstadoCitas.PENDIENTE) {
            throw new CitaException("Solo se pueden confirmar citas pendientes");
        }
    }

    private void validarEstadoCompletar(Cita cita) {
        if (cita.getEstado() != EstadoCitas.CONFIRMADA) {
            throw new CitaException("Solo se pueden completar citas confirmadas");
        }
    }

    // ============= MÉTODOS PRIVADOS DE NOTIFICACIÓN =============

    private void enviarNotificacionConfirmacion(Cita cita) {
        String emailPaciente = cita.getPaciente().getAccount().getEmail();
        LocalDateTime fechaHora = cita.getFechaHora().atZone(ZoneId.systemDefault()).toLocalDateTime();
        emailService.enviarCorreoConfirmacionCita(emailPaciente, cita.getOdontologo().getName(), fechaHora);
    }

    private void enviarNotificacionCompletada(Cita cita) {
        String emailPaciente = cita.getPaciente().getAccount().getEmail();
        LocalDateTime fechaHora = cita.getFechaHora().atZone(ZoneId.systemDefault()).toLocalDateTime();
        emailService.enviarCorreoCitaCompletada(emailPaciente, cita.getOdontologo().getName(), fechaHora);
    }

    private void enviarNotificacionReprogramacion(Cita cita) {
        String emailPaciente = cita.getPaciente().getAccount().getEmail();
        LocalDateTime fechaHora = cita.getFechaHora().atZone(ZoneId.systemDefault()).toLocalDateTime();
        emailService.enviarCorreoReprogramacionCita(emailPaciente, cita.getOdontologo().getName(), fechaHora);
    }

    private void enviarNotificacionRecordatorio(Cita cita) {
        String emailPaciente = cita.getPaciente().getAccount().getEmail();
        LocalDateTime fechaHora = cita.getFechaHora().atZone(ZoneId.systemDefault()).toLocalDateTime();
        emailService.enviarCorreoRecordatorioCita(emailPaciente, cita.getOdontologo().getName(), fechaHora);
    }
}