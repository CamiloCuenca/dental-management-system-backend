package edu.uniquindio.dentalmanagementsystembackend.dto.cita;

import java.time.LocalDate;
import java.time.LocalTime;

public record EditarCitaNoAutenticadaAdminDTO(
    String nombrePaciente,
    String numeroIdentificacion,
    String telefono,
    String email,
    String doctorId,
    LocalDate fecha,
    LocalTime hora,
    Long tipoCitaId
) {} 