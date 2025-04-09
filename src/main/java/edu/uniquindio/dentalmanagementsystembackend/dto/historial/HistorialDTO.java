package edu.uniquindio.dentalmanagementsystembackend.dto.historial;

import java.time.LocalDate;

/**
 * DTO para la visualización de un historial médico.
 * Contiene todos los datos necesarios para mostrar un registro del historial médico,
 * incluyendo información adicional como nombres de paciente y odontólogo.
 */
public record HistorialDTO(
    /**
     * ID único del historial médico.
     */
    Long id,
    
    /**
     * Nombre completo del paciente.
     */
    String nombrePaciente,
    
    /**
     * Nombre completo del odontólogo.
     */
    String nombreOdontologo,
    
    /**
     * Fecha en que se realizó la consulta.
     */
    LocalDate fecha,
    
    /**
     * Diagnóstico realizado por el odontólogo.
     */
    String diagnostico,
    
    /**
     * Tratamiento prescrito por el odontólogo.
     */
    String tratamiento,
    
    /**
     * Observaciones adicionales del odontólogo.
     */
    String observaciones,
    

    /**
     * Tipo de cita asociada al historial.
     */
    String tipoCita
) {} 