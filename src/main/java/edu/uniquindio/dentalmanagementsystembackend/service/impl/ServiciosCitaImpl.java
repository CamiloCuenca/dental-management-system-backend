package edu.uniquindio.dentalmanagementsystembackend.service.impl;

import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoCitas;
import edu.uniquindio.dentalmanagementsystembackend.Enum.Rol;
import edu.uniquindio.dentalmanagementsystembackend.Enum.TipoCita;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.CitaDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.ListaCitasDTO;
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
        // Buscar el paciente en la base de datos
        User paciente = userRepository.findById(citaDTO.idPaciente())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        // Verificar que el usuario es un paciente
        Hibernate.initialize(paciente.getAccount()); // Asegurar carga de la cuenta
        if (paciente.getAccount() == null || paciente.getAccount().getRol() != Rol.PACIENTE) {
            throw new IllegalArgumentException("El usuario con ID " + citaDTO.idPaciente() + " no es un paciente.");
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

        if (odontologoAsignado == null || fechaHoraAsignada == null) {
            throw new IllegalArgumentException("No se encontró disponibilidad para programar la cita.");
        }

        // Convertir a Instant y guardar la cita
        Instant instant = fechaHoraAsignada.atZone(ZoneId.systemDefault()).toInstant();
        Cita cita = new Cita(paciente, odontologoAsignado, instant, citaDTO.estado(), citaDTO.tipoCita());
        citasRepository.save(cita);
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
        return citasRepository.findByPacienteId(String.valueOf(idPaciente)).stream()
                .map(cita -> new ListaCitasDTO(
                        cita.getId(),
                        Long.parseLong(cita.getPaciente().getIdNumber()),  // Convertir String a Long
                        Long.parseLong(cita.getOdontologo().getIdNumber()), // Convertir String a Long
                        cita.getFechaHora().atZone(ZoneId.systemDefault()).toLocalDateTime(), // Convertir Instant a LocalDateTime
                        cita.getEstado(),
                        cita.getTipoCita()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todas las citas del sistema.
     *
     * @return Lista de objetos ListaCitasDTO que contienen todas las citas.
     */
    @Override
    public List<ListaCitasDTO> obtenerTodasLasCitas() {
        return citasRepository.findAll().stream()
                .map(cita -> new ListaCitasDTO(
                        cita.getId(),
                        Long.parseLong(cita.getPaciente().getIdNumber()),
                        Long.parseLong(cita.getOdontologo().getIdNumber()),
                        cita.getFechaHora().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                        cita.getEstado(),
                        cita.getTipoCita()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Edita el tipo de una cita existente.
     *
     * @param idCita ID de la cita a editar.
     * @param nuevoTipoCita Nuevo tipo de cita.
     */
    @Override
    public void editarCita(Long idCita, TipoCita nuevoTipoCita) {
        Cita cita = citasRepository.findById(idCita)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        cita.setTipoCita(nuevoTipoCita);
        citasRepository.save(cita);
        System.out.println("Cita actualizada correctamente.");
    }

    /**
     * Cancela una cita existente cambiando su estado a CANCELADA.
     *
     * @param idCita ID de la cita a cancelar.
     */
    @Override
    public void cancelarCita(Long idCita) {
        Cita cita = citasRepository.findById(idCita)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        cita.setEstado(EstadoCitas.CANCELADA);
        citasRepository.save(cita);
        System.out.println("Cita cancelada correctamente.");
    }
}