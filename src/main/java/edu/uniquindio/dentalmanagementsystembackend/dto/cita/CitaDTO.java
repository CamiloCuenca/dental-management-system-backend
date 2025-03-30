package edu.uniquindio.dentalmanagementsystembackend.dto.cita;

import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoCitas;
import edu.uniquindio.dentalmanagementsystembackend.Enum.TipoCita;

import java.time.LocalDateTime;

public record CitaDTO(
        Long idPaciente,
        EstadoCitas estado,
        TipoCita tipoCita,
        Long idDoctor,
        LocalDateTime fechaHora
) {
}
