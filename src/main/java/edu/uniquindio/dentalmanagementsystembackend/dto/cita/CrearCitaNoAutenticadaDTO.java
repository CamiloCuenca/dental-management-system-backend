package edu.uniquindio.dentalmanagementsystembackend.dto.cita;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

public record CrearCitaNoAutenticadaDTO(
        // Información del paciente
        String nombrePaciente,
        String pacienteId,
        String telefono,
        String email,
        
        // Información de la cita
        String doctorId,
        LocalDate fecha,
        LocalTime hora,
        Long tipoCitaId
) {
    public LocalDateTime getFechaHora() {
        return LocalDateTime.of(fecha, hora);
    }
} 