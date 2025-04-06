package edu.uniquindio.dentalmanagementsystembackend.dto.cita;

/**
 * DTO para mostrar la información esencial de un tipo de cita.
 * Contiene solo los datos necesarios para que un paciente pueda seleccionar un tipo de cita.
 */
public record TipoCitaDTO(
    Long id,
    String nombre,
    int duracionMinutos,
    String descripcion
) {} 