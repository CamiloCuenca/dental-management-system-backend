package edu.uniquindio.dentalmanagementsystembackend.service.impl;

import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoCitas;
import edu.uniquindio.dentalmanagementsystembackend.Enum.Rol;
import edu.uniquindio.dentalmanagementsystembackend.Enum.TipoCita;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.CitaDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.ListaCitasDTO;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.User;
import edu.uniquindio.dentalmanagementsystembackend.entity.Cita;
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
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

// Anotación que indica que esta clase es un servicio de Spring
@Transactional
@Service
public class ServiciosCitaImpl implements ServiciosCitas {

    // Inyección de dependencias para el repositorio de citas
    @Autowired
    private CitasRepository citasRepository;

    // Inyección de dependencias para el repositorio de cuentas
    @Autowired
    private CuentaRepository cuentaRepository; // Para obtener pacientes y doctores

    // Inyección de dependencias para el repositorio de usuarios
    @Autowired
    private UserRepository userRepository;

    @Autowired
    EmailService emailService;

    /**
     * Crea una nueva cita en el sistema.
     *
     * @param citaDTO Objeto que contiene los datos de la cita a crear.
     */
    @Override
    @Transactional
    public void crearCita(CitaDTO citaDTO) throws Exception {
        // Validar que el ID del paciente no sea nulo o negativo
        if (citaDTO.idPaciente() == null || citaDTO.idPaciente() <= 0) {
            throw new IllegalArgumentException("El ID del paciente no es válido.");
        }

        // Buscar el paciente en la base de datos
        User paciente = userRepository.findById(citaDTO.idPaciente())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        // Verificar que el usuario es un paciente
        Hibernate.initialize(paciente.getAccount()); // Asegurar carga de la cuenta
        if (paciente.getAccount() == null || paciente.getAccount().getRol() != Rol.PACIENTE) {
            throw new IllegalArgumentException("El usuario con ID " + citaDTO.idPaciente() + " no es un paciente.");
        }

        // Validar que el tipo de cita y el estado no sean nulos
        if (citaDTO.tipoCita() == null) {
            throw new IllegalArgumentException("El tipo de cita no puede ser nulo.");
        }
        if (citaDTO.estado() == null) {
            throw new IllegalArgumentException("El estado de la cita no puede ser nulo.");
        }

        // Validar que el paciente no tenga otra cita el mismo día
        LocalDate hoy = LocalDate.now();
        Instant inicioDelDia = hoy.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant finDelDia = hoy.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant();

        boolean tieneCitaHoy = citasRepository.findByPacienteAndFechaHoraBetween(paciente, inicioDelDia, finDelDia)
                .stream()
                .anyMatch(cita -> cita.getFechaHora().atZone(ZoneOffset.UTC).toLocalDate().equals(hoy));

        if (tieneCitaHoy) {
            throw new IllegalArgumentException("El paciente ya tiene una cita programada para hoy.");
        }

        // Obtener odontólogos disponibles
        List<User> odontologos = userRepository.findByAccount_Rol(Rol.DOCTOR);
        if (odontologos.isEmpty()) {
            throw new IllegalArgumentException("No hay odontólogos disponibles.");
        }

        // Buscar odontólogo con disponibilidad
        User odontologoAsignado = null;
        LocalDateTime fechaHoraAsignada = null;
        LocalDate fecha = LocalDate.now();
        LocalTime horaInicio = LocalTime.of(8, 0);
        LocalTime horaFin = LocalTime.of(18, 0);

        for (User odontologo : odontologos) {
            while (odontologoAsignado == null) {
                List<Cita> citasDelDia = citasRepository.findByOdontologoAndFecha(odontologo, fecha);
                LocalDateTime posibleHora = LocalDateTime.of(fecha, horaInicio);

                while (posibleHora.toLocalTime().isBefore(horaFin)) {
                    LocalDateTime finalPosibleHora = posibleHora;
                    boolean disponible = citasDelDia.stream().noneMatch(cita ->
                            cita.getFechaHora() != null &&
                                    Math.abs(ChronoUnit.MINUTES.between(cita.getFechaHora().atZone(ZoneOffset.UTC).toLocalDateTime(), finalPosibleHora)) < 40);

                    if (disponible) {
                        odontologoAsignado = odontologo;
                        fechaHoraAsignada = posibleHora;
                        break;
                    }
                    posibleHora = posibleHora.plusMinutes(40);
                }
                if (odontologoAsignado == null) {
                    fecha = fecha.plusDays(1);
                }
            }
            if (odontologoAsignado != null) break;
        }

        if (fechaHoraAsignada.getHour() < 8 || fechaHoraAsignada.getHour() >= 18) {
            throw new IllegalArgumentException("Las citas solo pueden programarse entre las 08:00 y las 18:00.");
        }

        // Validar si se encontró un odontólogo y horario disponible
        if (odontologoAsignado == null || fechaHoraAsignada == null) {
            throw new IllegalArgumentException("No se encontró disponibilidad para programar la cita.");
        }

        if (fechaHoraAsignada.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha de la cita debe ser en el futuro.");
        }

        // Convertir a Instant y guardar la cita
        Instant instant = fechaHoraAsignada.atZone(ZoneId.systemDefault()).toInstant();
        Cita cita = new Cita(paciente, odontologoAsignado, instant, citaDTO.estado(), citaDTO.tipoCita());
        citasRepository.save(cita);

        // Enviar correo al paciente con los detalles de la cita
        String emailPaciente = paciente.getAccount().getEmail();
        emailService.enviarCorreoCita(emailPaciente, odontologoAsignado.getName(), fechaHoraAsignada.toString());
        System.out.println("✅ Cita creada correctamente con el odontólogo " + odontologoAsignado.getName() + " en la fecha: " + fechaHoraAsignada);
    }

    /**
     * Obtiene todas las citas de un paciente.
     *
     * @param idPaciente ID del paciente.
     * @return Lista de objetos ListaCitasDTO que contienen las citas del paciente.
     */
    @Override
    public List<ListaCitasDTO> obtenerCitasPorPaciente(Long idPaciente) {
        // Validar que el ID del paciente no sea nulo o negativo
        if (idPaciente == null || idPaciente <= 0) {
            throw new IllegalArgumentException("El ID del paciente no es válido.");
        }

        // Verificar que el paciente existe en la base de datos
        if (!userRepository.existsById(idPaciente)) {
            throw new RuntimeException("El paciente con ID " + idPaciente + " no existe.");
        }

        // Obtener las citas del paciente
        List<Cita> citas = citasRepository.findByPacienteId(String.valueOf(idPaciente));

        // Validar si el paciente tiene citas registradas
        if (citas.isEmpty()) {
            throw new RuntimeException("El paciente con ID " + idPaciente + " no tiene citas registradas.");
        }

        // Mapear citas a DTOs con validaciones adicionales
        return citas.stream()
                .map(cita -> {
                    try {
                        // Validar que los atributos de la cita no sean nulos antes de acceder a ellos
                        if (cita.getPaciente() == null || cita.getOdontologo() == null || cita.getFechaHora() == null) {
                            throw new RuntimeException("Error en los datos de la cita con ID " + cita.getId());
                        }

                        return new ListaCitasDTO(
                                cita.getId(),
                                Long.parseLong(cita.getPaciente().getIdNumber()),  // Convertir String a Long
                                Long.parseLong(cita.getOdontologo().getIdNumber()), // Convertir String a Long
                                cita.getFechaHora().atZone(ZoneId.systemDefault()).toLocalDateTime(), // Convertir Instant a LocalDateTime
                                cita.getEstado(),
                                cita.getTipoCita()
                        );
                    } catch (NumberFormatException e) {
                        throw new RuntimeException("Error al convertir el ID de la cita con ID " + cita.getId() + " a tipo Long.", e);
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todas las citas del sistema.
     *
     * @return Lista de objetos ListaCitasDTO que contienen todas las citas.
     */
    @Override
    public List<ListaCitasDTO> obtenerTodasLasCitas() {
        // Obtener todas las citas
        List<Cita> citas = citasRepository.findAll();

        // Validar si hay citas registradas
        if (citas.isEmpty()) {
            throw new RuntimeException("No hay citas registradas en el sistema.");
        }

        // Mapear citas a DTOs con validaciones adicionales
        return citas.stream()
                .map(cita -> {
                    try {
                        // Validar que los atributos de la cita no sean nulos antes de acceder a ellos
                        if (cita.getPaciente() == null || cita.getOdontologo() == null || cita.getFechaHora() == null) {
                            throw new RuntimeException("Error en los datos de la cita con ID " + cita.getId());
                        }

                        return new ListaCitasDTO(
                                cita.getId(),
                                Long.parseLong(cita.getPaciente().getIdNumber()),  // Convertir String a Long
                                Long.parseLong(cita.getOdontologo().getIdNumber()), // Convertir String a Long
                                cita.getFechaHora().atZone(ZoneId.systemDefault()).toLocalDateTime(), // Convertir Instant a LocalDateTime
                                cita.getEstado(),
                                cita.getTipoCita()
                        );
                    } catch (NumberFormatException e) {
                        throw new RuntimeException("Error al convertir el ID de la cita con ID " + cita.getId() + " a tipo Long.", e);
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * Edita el tipo de una cita existente.
     *
     * @param idCita ID de la cita a editar.
     * @param nuevoTipoCita Nuevo tipo de cita.
     */
    @Override
    @Transactional
    public void editarCita(Long idCita, TipoCita nuevoTipoCita) {
        // Validar que el ID de la cita sea válido
        if (idCita == null || idCita <= 0) {
            throw new IllegalArgumentException("El ID de la cita no es válido.");
        }

        // Validar que el nuevo tipo de cita no sea nulo
        if (nuevoTipoCita == null) {
            throw new IllegalArgumentException("El nuevo tipo de cita no puede ser nulo.");
        }

        // Buscar la cita en la base de datos
        Cita cita = citasRepository.findById(idCita)
                .orElseThrow(() -> new RuntimeException("Cita con ID " + idCita + " no encontrada."));

        // Verificar si la cita está en estado cancelado o finalizado
        if (cita.getEstado() == EstadoCitas.CANCELADA || cita.getEstado() == EstadoCitas.COMPLETADA) {
            throw new IllegalStateException("No se puede editar una cita que está cancelada o finalizada.");
        }

        // Obtener la fecha y hora actual
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime fechaCita = cita.getFechaHora().atZone(ZoneId.systemDefault()).toLocalDateTime();

        // Validar que la cita no se pueda modificar si falta menos de 24 horas
        if (Duration.between(ahora, fechaCita).toHours() < 24) {
            throw new IllegalStateException("No se puede modificar la cita con ID " + idCita + " porque faltan menos de 24 horas para su inicio.");
        }

        // Actualizar el tipo de cita
        cita.setTipoCita(nuevoTipoCita);
        citasRepository.save(cita);

        System.out.println("✅ Cita con ID " + idCita + " actualizada correctamente a tipo: " + nuevoTipoCita);
    }

    /**
     * Cancela una cita existente cambiando su estado a CANCELADA.
     *
     * @param idCita ID de la cita a cancelar.
     */
    @Override
    @Transactional
    public void cancelarCita(Long idCita) {
        // Validar que el ID de la cita sea válido
        if (idCita == null || idCita <= 0) {
            throw new IllegalArgumentException("El ID de la cita no es válido.");
        }

        // Buscar la cita en la base de datos
        Cita cita = citasRepository.findById(idCita)
                .orElseThrow(() -> new RuntimeException("Cita con ID " + idCita + " no encontrada."));

        // Verificar si la cita ya está cancelada o finalizada
        if (cita.getEstado() == EstadoCitas.CANCELADA) {
            throw new IllegalStateException("La cita con ID " + idCita + " ya está cancelada.");
        }
        if (cita.getEstado() == EstadoCitas.COMPLETADA) {
            throw new IllegalStateException("No se puede cancelar una cita que ya ha finalizado.");
        }

        // Cancelar la cita
        cita.setEstado(EstadoCitas.CANCELADA);
        citasRepository.save(cita);

        System.out.println("✅ Cita con ID " + idCita + " cancelada correctamente.");
    }
}