package edu.uniquindio.dentalmanagementsystembackend.dto.cita;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

public record EditarCitaPacienteDTO(
        @NotNull Long citaId,
        LocalDate fecha,
        LocalTime hora
) {
} 