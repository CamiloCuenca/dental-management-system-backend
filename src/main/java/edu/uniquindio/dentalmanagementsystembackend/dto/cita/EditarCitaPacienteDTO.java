package edu.uniquindio.dentalmanagementsystembackend.dto.cita;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record EditarCitaPacienteDTO(
        @NotNull Long citaId,
        @NotNull Instant fechaHora
) {
} 