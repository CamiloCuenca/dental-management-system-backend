package edu.uniquindio.dentalmanagementsystembackend.dto.cita;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * DTO para mostrar la información de disponibilidad de un doctor.
 * Contiene los días y horarios en que el doctor está disponible.
 */
public record DisponibilidadDTO(
    DayOfWeek diaSemana,
    LocalTime horaInicio,
    LocalTime horaFin
) {} 