package edu.uniquindio.dentalmanagementsystembackend.dto.cita;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Future;

public record CrearCitaDTO(
        @NotBlank(message = "El ID del paciente no puede estar vacío.")
        String pacienteId,
        
        @NotBlank(message = "El ID del doctor no puede estar vacío.")
        String doctorId,
        
        @NotNull(message = "La fecha no puede ser nula.")
        @Future(message = "La fecha debe ser en el futuro.")
        LocalDate fecha,
        
        @NotNull(message = "La hora no puede ser nula.")
        LocalTime hora,
        
        @NotNull(message = "El ID del tipo de cita no puede ser nulo.")
        Long tipoCitaId
) {
    public LocalDateTime getFechaHora() {
        return LocalDateTime.of(fecha, hora);
    }
}
