package edu.uniquindio.dentalmanagementsystembackend.dto;

import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoCitas;
import edu.uniquindio.dentalmanagementsystembackend.Enum.TipoCita;

import java.time.LocalDateTime;

public record ListaCitasDTO(
        Integer idCita,
        Long idPaciente,
        Long idDoctor,
        LocalDateTime fechaHora,
        EstadoCitas estado,
        TipoCita tipoCita
) {
}
