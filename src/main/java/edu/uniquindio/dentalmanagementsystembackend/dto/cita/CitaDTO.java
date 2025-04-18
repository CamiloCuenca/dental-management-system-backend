package edu.uniquindio.dentalmanagementsystembackend.dto.cita;

import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoCitas;

import java.time.Instant;

/**
 * DTO para mostrar la información de una cita en las respuestas de la API.
 * Contiene solo los datos necesarios para mostrar una cita al usuario.
 */
public record CitaDTO(
    Long id,
    String pacienteId,
    String pacienteNombre,
    String doctorId,
    String doctorNombre,
    Instant fechaHora,
    EstadoCitas estado,
    String email,
    String telefono,
    Long tipoCitaId,
    String tipoCitaNombre,
    int duracionMinutos
) {} 