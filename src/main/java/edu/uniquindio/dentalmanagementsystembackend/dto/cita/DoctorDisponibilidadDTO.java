package edu.uniquindio.dentalmanagementsystembackend.dto.cita;

import java.time.LocalDateTime;
import java.util.List;

public record DoctorDisponibilidadDTO(String doctorId, List<LocalDateTime> fechasDisponibles) {
}