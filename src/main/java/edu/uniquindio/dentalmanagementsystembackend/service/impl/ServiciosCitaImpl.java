package edu.uniquindio.dentalmanagementsystembackend.service.impl;

import edu.uniquindio.dentalmanagementsystembackend.dto.CitaDTO;
import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoCitas;
import edu.uniquindio.dentalmanagementsystembackend.entity.Cita;
import edu.uniquindio.dentalmanagementsystembackend.entity.Usuario;
import edu.uniquindio.dentalmanagementsystembackend.repository.CitasRepository;
import edu.uniquindio.dentalmanagementsystembackend.repository.UsuarioRepository;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.ServiciosCitas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;

@Service
public class ServiciosCitaImpl implements ServiciosCitas {

    @Autowired
    private CitasRepository citasRepository;

    @Autowired
    private UsuarioRepository usuarioRepository; // Para obtener pacientes y doctores

    @Override
    public void crearCita(CitaDTO citaDTO) {
        Usuario paciente = usuarioRepository.findById(citaDTO.idPaciente())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        Usuario odontologo = usuarioRepository.findById(citaDTO.idDoctor())
                .orElseThrow(() -> new RuntimeException("Odontólogo no encontrado"));

        Instant fechaInstant = citaDTO.fechaHora().toInstant(ZoneOffset.UTC);

        Cita cita = new Cita(paciente, odontologo, fechaInstant, citaDTO.estado());
        citasRepository.save(cita);
        System.out.println("Cita creada correctamente.");
    }
}
