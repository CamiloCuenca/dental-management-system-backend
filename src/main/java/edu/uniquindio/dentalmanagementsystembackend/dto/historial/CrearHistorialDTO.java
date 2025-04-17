package edu.uniquindio.dentalmanagementsystembackend.dto.historial;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * DTO para la creación de un nuevo historial médico.
 * Contiene todos los datos necesarios para crear un registro en el historial médico.
 */
public record CrearHistorialDTO(
    /**
     * ID del paciente al que pertenece el historial.
     */
    @NotNull(message = "El ID del paciente no puede ser nulo")
    String pacienteId,
    
    /**
     * ID del odontólogo que crea el historial.
     */
    @NotNull(message = "El ID del odontólogo no puede ser nulo")
    String odontologoId,
    
    /**
     * ID de la cita asociada al historial.
     */
    @NotNull(message = "El ID de la cita no puede ser nulo")
    Long citaId,
    
    /**
     * Fecha en que se realizó la consulta.
     */
    @NotNull(message = "La fecha no puede ser nula")
    LocalDate fecha,
    
    /**
     * Diagnóstico realizado por el odontólogo.
     */
    @NotBlank(message = "El diagnóstico no puede estar vacío")
    String diagnostico,
    
    /**
     * Tratamiento prescrito por el odontólogo.
     */
    @NotBlank(message = "El tratamiento no puede estar vacío")
    String tratamiento,
    
    /**
     * Observaciones adicionales del odontólogo.
     */
    String observaciones,
    
    /**
     * Fecha programada para la próxima cita.
     */
    LocalDate proximaCita
) {}
