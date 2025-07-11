package edu.uniquindio.dentalmanagementsystembackend.dto.cita;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;
import java.time.LocalDate;
import java.time.LocalTime;

public record EditarCitaPacienteDTO(
        @NotNull(message = "El ID de la cita no puede ser nulo.")
        Long citaId,
        
        @NotNull(message = "La fecha no puede ser nula.")
        @Future(message = "La fecha debe ser en el futuro.")
        LocalDate fecha,
        
        @NotNull(message = "La hora no puede ser nula.")
        LocalTime hora
) {
} 