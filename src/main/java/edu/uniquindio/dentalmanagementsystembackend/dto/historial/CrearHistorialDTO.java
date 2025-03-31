package edu.uniquindio.dentalmanagementsystembackend.dto.historial;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CrearHistorialDTO(
    @NotNull(message = "El ID del paciente no puede ser nulo")
    Long pacienteId,
    
    @NotNull(message = "El ID del odontólogo no puede ser nulo")
    Long odontologoId,
    
    @NotNull(message = "La fecha no puede ser nula")
    LocalDate fecha,
    
    @NotBlank(message = "El diagnóstico no puede estar vacío")
    String diagnostico,
    
    @NotBlank(message = "El tratamiento no puede estar vacío")
    String tratamiento,
    
    String observaciones,
    
    LocalDate proximaCita
) {}
