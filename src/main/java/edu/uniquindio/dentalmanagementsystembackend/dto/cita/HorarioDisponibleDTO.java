package edu.uniquindio.dentalmanagementsystembackend.dto.cita;

import java.time.LocalTime;

/**
 * DTO para mostrar los horarios disponibles.
 * Contiene la hora y si está disponible o no.
 */
public record HorarioDisponibleDTO(
    LocalTime hora,
    boolean disponible
) {} 