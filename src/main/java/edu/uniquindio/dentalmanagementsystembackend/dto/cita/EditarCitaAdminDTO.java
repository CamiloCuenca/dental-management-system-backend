package edu.uniquindio.dentalmanagementsystembackend.dto.cita;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record EditarCitaAdminDTO(
        @NotNull Long citaId,
        @NotNull String pacienteId,
        @NotNull String odontologoId,
        @NotNull Instant fechaHora
) {
} 