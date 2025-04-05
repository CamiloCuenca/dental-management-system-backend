package edu.uniquindio.dentalmanagementsystembackend.dto.email;

/**
 * DTO para el envío de correos de confirmación de citas
 */
public record CitaEmailDTO(
    String emailPaciente,
    String nombrePaciente,
    String nombreOdontologo,
    String fechaHora,
    String tipoCita
) {} 