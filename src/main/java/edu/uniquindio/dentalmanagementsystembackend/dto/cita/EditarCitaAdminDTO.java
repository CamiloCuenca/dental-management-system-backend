package edu.uniquindio.dentalmanagementsystembackend.dto.cita;

import edu.uniquindio.dentalmanagementsystembackend.Enum.TipoCita;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record EditarCitaAdminDTO(
        @NotNull Long citaId,
        @NotNull Long pacienteId,
        @NotNull Long odontologoId,
        @NotNull Instant fechaHora,
        @NotNull TipoCita tipoCita
) {
} 