package edu.uniquindio.dentalmanagementsystembackend.dto.cita;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record CrearCitaDTO(
        @NotNull Long pacienteId,
        @NotNull Long odontologoId,
        @NotNull Instant fechaHora,
        @NotNull Long tipoCitaId
) {
}
