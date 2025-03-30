package edu.uniquindio.dentalmanagementsystembackend.service.impl;

import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoCitas;
import edu.uniquindio.dentalmanagementsystembackend.Enum.Rol;
import edu.uniquindio.dentalmanagementsystembackend.Enum.TipoCita;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.CitaDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.ListaCitasDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.DoctorDisponibilidadDTO;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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

        // Validar que el ID del doctor no sea nulo o negativo
        if (citaDTO.idDoctor() == null || citaDTO.idDoctor() <= 0) {
            throw new IllegalArgumentException("El ID del doctor no es válido.");
        }

        // Validar que la fecha y hora no sean nulas
        if (citaDTO.fechaHora() == null) {
            throw new IllegalArgumentException("La fecha y hora de la cita no pueden ser nulas.");
        }

        // Buscar el paciente en la base de datos
        User paciente = userRepository.findById(citaDTO.idPaciente())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        // Verificar que el usuario es un paciente
        Hibernate.initialize(paciente.getAccount()); // Asegurar carga de la cuenta
        if (paciente.getAccount() == null || paciente.getAccount().getRol() != Rol.PACIENTE) {
            throw new IllegalArgumentException("El usuario con ID " + citaDTO.idPaciente() + " no es un paciente.");
        }

        // Buscar el doctor en la base de datos
        User doctor = userRepository.findById(citaDTO.idDoctor())
                .orElseThrow(() -> new RuntimeException("Doctor no encontrado"));

        // Verificar que el usuario es un doctor
        Hibernate.initialize(doctor.getAccount()); // Asegurar carga de la cuenta
        if (doctor.getAccount() == null || doctor.getAccount().getRol() != Rol.DOCTOR) {
            throw new IllegalArgumentException("El usuario con ID " + citaDTO.idDoctor() + " no es un doctor.");
        }

        // Validar que el tipo de cita y el estado no sean nulos
        if (citaDTO.tipoCita() == null) {
            throw new IllegalArgumentException("El tipo de cita no puede ser nulo.");
        }
        if (citaDTO.estado() == null) {
            throw new IllegalArgumentException("El estado de la cita no puede ser nulo.");
        }

        // Validar que el paciente no tenga otra cita el mismo día
        LocalDate fechaCita = citaDTO.fechaHora().toLocalDate();
        Instant inicioDelDia = fechaCita.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant finDelDia = fechaCita.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant();

        boolean tieneCitaEseDia = citasRepository.findByPacienteAndFechaHoraBetween(paciente, inicioDelDia, finDelDia)
                .stream()
                .anyMatch(cita -> cita.getFechaHora().atZone(ZoneOffset.UTC).toLocalDate().equals(fechaCita));

        if (tieneCitaEseDia) {
            throw new IllegalArgumentException("El paciente ya tiene una cita programada para ese día.");
        }

        // Validar que el doctor no tenga otra cita en la misma fecha y hora
        boolean doctorOcupado = citasRepository.findByOdontologoAndFechaHoraBetween(doctor, inicioDelDia, finDelDia)
                .stream()
                .anyMatch(cita -> cita.getFechaHora().atZone(ZoneOffset.UTC).toLocalDateTime().equals(citaDTO.fechaHora()));

        if (doctorOcupado) {
            throw new IllegalArgumentException("El doctor ya tiene una cita programada para esa fecha y hora.");
        }

        // Convertir a Instant y guardar la cita
        Instant instant = citaDTO.fechaHora().atZone(ZoneId.systemDefault()).toInstant();
        Cita cita = new Cita(paciente, doctor, instant, citaDTO.estado(), citaDTO.tipoCita());
        citasRepository.save(cita);

        // Enviar correo al paciente con los detalles de la cita
        String emailPaciente = paciente.getAccount().getEmail();
        emailService.enviarCorreoCita(emailPaciente, doctor.getName(), citaDTO.fechaHora().toString());
        System.out.println("✅ Cita creada correctamente con el doctor " + doctor.getName() + " en la fecha: " + citaDTO.fechaHora());
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


    @Override
    public List<DoctorDisponibilidadDTO> obtenerFechasDisponiblesDoctores() {
        List<DoctorDisponibilidadDTO> disponibilidadDoctores = new ArrayList<>();

        // Obtener todos los doctores
        List<User> doctores = userRepository.findAll();

        for (User doctor : doctores) {
            List<LocalDateTime> fechasDisponibles = obtenerFechasDisponibles(doctor);

            // Limitar a un máximo de 5 fechas
            if (fechasDisponibles.size() > 5) {
                fechasDisponibles = fechasDisponibles.subList(0, 5);
            }

            disponibilidadDoctores.add(new DoctorDisponibilidadDTO(doctor.getIdNumber(), fechasDisponibles));
        }

        return disponibilidadDoctores;
    }

   private List<LocalDateTime> obtenerFechasDisponibles(User doctor) {
       List<LocalDateTime> fechasDisponibles = new ArrayList<>();
       LocalDateTime ahora = LocalDateTime.now();
       LocalDateTime tresMesesAdelante = ahora.plusMonths(3);
       LocalTime horaInicio = LocalTime.of(8, 0);
       LocalTime horaFin = LocalTime.of(18, 0);

       // Obtener todas las citas del doctor en los próximos tres meses
       List<Cita> citas = citasRepository.findByOdontologoAndFechaHoraBetween(doctor, ahora.toInstant(ZoneOffset.UTC), tresMesesAdelante.toInstant(ZoneOffset.UTC));

       // Crear un conjunto de fechas ocupadas
       Set<LocalDateTime> fechasOcupadas = citas.stream()
               .map(cita -> cita.getFechaHora().atZone(ZoneId.systemDefault()).toLocalDateTime())
               .collect(Collectors.toSet());

       // Iterar sobre cada día en el rango de tres meses
       for (LocalDateTime fecha = ahora; fecha.isBefore(tresMesesAdelante); fecha = fecha.plusDays(1)) {
           // Iterar sobre cada intervalo de 40 minutos en el horario de trabajo
           for (LocalDateTime hora = fecha.with(horaInicio); hora.isBefore(fecha.with(horaFin)); hora = hora.plusMinutes(40)) {
               // Si la fecha y hora no están ocupadas, añadir a la lista de fechas disponibles
               if (!fechasOcupadas.contains(hora)) {
                   fechasDisponibles.add(hora);
               }
           }
       }

       return fechasDisponibles;
   }

}