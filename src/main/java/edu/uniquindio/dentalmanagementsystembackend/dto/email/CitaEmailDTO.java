package edu.uniquindio.dentalmanagementsystembackend.dto.email;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para el envío de correos de confirmación de citas
 */
public record CitaEmailDTO(
        @NotBlank(message = "El email del paciente no puede estar vacío.")
        @Size(max = 100, message = "El email no puede exceder 100 caracteres.")
        String emailPaciente,
        
        @NotBlank(message = "El nombre del paciente no puede estar vacío.")
        @Size(max = 100, message = "El nombre del paciente no puede exceder 100 caracteres.")
        String nombrePaciente,
        
        @NotBlank(message = "El nombre del odontólogo no puede estar vacío.")
        @Size(max = 100, message = "El nombre del odontólogo no puede exceder 100 caracteres.")
        String nombreOdontologo,
        
        @NotBlank(message = "La fecha y hora no puede estar vacía.")
        String fechaHora,
        
        @NotBlank(message = "El tipo de cita no puede estar vacío.")
        @Size(max = 50, message = "El tipo de cita no puede exceder 50 caracteres.")
        String tipoCita
) {} 