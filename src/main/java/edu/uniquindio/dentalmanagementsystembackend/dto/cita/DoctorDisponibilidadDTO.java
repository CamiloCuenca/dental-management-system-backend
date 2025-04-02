package edu.uniquindio.dentalmanagementsystembackend.dto.cita;

import java.time.LocalDateTime;
import java.util.List;

public record DoctorDisponibilidadDTO(
        String doctorId,           // ID del doctor
        String nombreDoctor,       // Nombre completo del doctor
        String tipoDoctor,         // Tipo de doctor (especialidad)
        List<LocalDateTime> fechasDisponibles
) {}