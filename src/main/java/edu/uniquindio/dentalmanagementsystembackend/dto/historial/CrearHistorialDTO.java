package edu.uniquindio.dentalmanagementsystembackend.dto.historial;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
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
    @Past(message = "La fecha debe ser en el pasado")
    LocalDate fecha,
    
    /**
     * Diagnóstico realizado por el odontólogo.
     */
    @NotBlank(message = "El diagnóstico no puede estar vacío")
    @Size(max = 1000, message = "El diagnóstico no puede exceder 1000 caracteres")
    String diagnostico,
    
    /**
     * Tratamiento prescrito por el odontólogo.
     */
    @NotBlank(message = "El tratamiento no puede estar vacío")
    @Size(max = 1000, message = "El tratamiento no puede exceder 1000 caracteres")
    String tratamiento,
    
    /**
     * Observaciones adicionales del odontólogo.
     */
    @Size(max = 500, message = "Las observaciones no pueden exceder 500 caracteres")
    String observaciones,
    
    /**
     * Fecha programada para la próxima cita.
     */
    LocalDate proximaCita
) {}
