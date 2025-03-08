package edu.uniquindio.dentalmanagementsystembackend.dto.cita;

import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoCitas;
import edu.uniquindio.dentalmanagementsystembackend.Enum.TipoCita;

import java.time.LocalDateTime;

public record CitaDTO(
        Long idPaciente,
        Long idDoctor,
        LocalDateTime fechaHora,
        EstadoCitas estado,
        TipoCita tipoCita
) {
}
