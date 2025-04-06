package edu.uniquindio.dentalmanagementsystembackend.dto.cita;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO para mostrar las fechas disponibles con sus horarios.
 * Contiene la fecha y los horarios disponibles para esa fecha.
 */
public record FechaDisponibleDTO(
    LocalDate fecha,
    List<HorarioDisponibleDTO> horarios
) {} 