package edu.uniquindio.dentalmanagementsystembackend.dto.cita;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record EditarCitaAdminDTO(
        @NotNull Long citaId,
        @NotNull String pacienteId,
        @NotNull String odontologoId,
        @NotNull LocalDate fecha,
        @NotNull LocalTime hora
) {
} 