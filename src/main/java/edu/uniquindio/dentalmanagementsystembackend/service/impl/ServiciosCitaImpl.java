package edu.uniquindio.dentalmanagementsystembackend.service.impl;

import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoCitas;
import edu.uniquindio.dentalmanagementsystembackend.Enum.Rol;
import edu.uniquindio.dentalmanagementsystembackend.Enum.TipoCita;
import edu.uniquindio.dentalmanagementsystembackend.dto.CitaDTO;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.User;
import edu.uniquindio.dentalmanagementsystembackend.entity.Cita;
import edu.uniquindio.dentalmanagementsystembackend.repository.CitasRepository;
import edu.uniquindio.dentalmanagementsystembackend.repository.CuentaRepository;
import edu.uniquindio.dentalmanagementsystembackend.repository.UserRepository;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.ServiciosCitas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiciosCitaImpl implements ServiciosCitas {

    @Autowired
    private CitasRepository citasRepository;

    @Autowired
    private CuentaRepository cuentaRepository; // Para obtener pacientes y doctores

    @Autowired
    private UserRepository userRepository;

    /**
     *
     * @param citaDTO
     */
    @Override
    public void crearCita(CitaDTO citaDTO) {
        // Buscar el paciente y el odontólogo en la base de datos
        User paciente = userRepository.findById(citaDTO.idPaciente())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        User odontologo = userRepository.findById(citaDTO.idDoctor())
                .orElseThrow(() -> new RuntimeException("Odontólogo no encontrado"));

        // Validación de roles
        if (paciente.getAccount() == null || paciente.getAccount().getRol() != Rol.PACIENTE) {
            throw new IllegalArgumentException("El usuario con ID " + citaDTO.idPaciente() + " no es un paciente.");
        }
        if (odontologo.getAccount() == null || odontologo.getAccount().getRol() != Rol.DOCTOR) {
            throw new IllegalArgumentException("El usuario con ID " + citaDTO.idDoctor() + " no es un odontólogo.");
        }

        // Convertir la fecha a Instant
        Instant fechaInstant = citaDTO.fechaHora().toInstant(ZoneOffset.UTC);

        // Crear la cita con User
        Cita cita = new Cita(paciente, odontologo, fechaInstant, citaDTO.estado() , citaDTO.tipoCita());
        citasRepository.save(cita);

        System.out.println("Cita creada correctamente.");
    }

    /**
     *
     * @param idPaciente
     * @return
     */
    @Override
    public List<CitaDTO> obtenerCitasPorPaciente(Long idPaciente) {
        return citasRepository.findByPacienteId(String.valueOf(idPaciente)).stream()
                .map(cita -> new CitaDTO(
                        Long.parseLong(cita.getPaciente().getIdNumber()),  // Convertir String a Long
                        Long.parseLong(cita.getOdontologo().getIdNumber()), // Convertir String a Long
                        cita.getFechaHora().atZone(ZoneId.systemDefault()).toLocalDateTime(), // Convertir Instant a LocalDateTime
                        cita.getEstado(),
                        cita.getTipoCita()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<CitaDTO> obtenerTodasLasCitas() {
        return citasRepository.findAll().stream()
                .map(cita -> new CitaDTO(
                        Long.parseLong(cita.getPaciente().getIdNumber()),
                        Long.parseLong(cita.getOdontologo().getIdNumber()),
                        cita.getFechaHora().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                        cita.getEstado(),
                        cita.getTipoCita()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public void editarCita(Long idCita, TipoCita nuevoTipoCita) {
        Cita cita = citasRepository.findById(idCita)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        cita.setTipoCita(nuevoTipoCita);
        citasRepository.save(cita);
        System.out.println("Cita actualizada correctamente.");
    }

    @Override
    public void cancelarCita(Long idCita) {
        Cita cita = citasRepository.findById(idCita)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        cita.setEstado(EstadoCitas.CANCELADA);
        citasRepository.save(cita);
        System.out.println("Cita cancelada correctamente.");
    }
}