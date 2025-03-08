package edu.uniquindio.dentalmanagementsystembackend.dto;

import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoCitas;
import edu.uniquindio.dentalmanagementsystembackend.Enum.TipoCita;

public record CitaDTO(
        Long idPaciente,
        EstadoCitas estado,
        TipoCita tipoCita
) {
}
