package edu.uniquindio.dentalmanagementsystembackend.dto.cita;

import java.util.List;

/**
 * DTO para mostrar la información esencial de un doctor por especialidad.
 * Contiene solo los datos necesarios para que un paciente pueda agendar una cita.
 */
public record DoctorEspecialidadDTO(
    String id,
    String nombre,
    String apellido,
    String especialidad,
    List<DisponibilidadDTO> disponibilidad
) {} 