package edu.uniquindio.dentalmanagementsystembackend.dto;

import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoCitas;
import java.time.LocalDateTime;

public record CitaDTO(
        Long idPaciente,
        Long idDoctor,
        LocalDateTime fechaHora,
        EstadoCitas estado
) {
}
