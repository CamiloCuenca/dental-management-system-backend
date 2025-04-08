package edu.uniquindio.dentalmanagementsystembackend.service.Interfaces;

import edu.uniquindio.dentalmanagementsystembackend.dto.cita.FechaDisponibleDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.HorarioDisponibleDTO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ServiciosDisponibilidadDoctor {
    
    /**
     * Obtiene las fechas disponibles para un doctor en un rango de fechas
     * @param doctorId ID del doctor
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha de fin del rango
     * @return Lista de fechas disponibles con sus horarios
     */
    List<FechaDisponibleDTO> obtenerFechasDisponibles(String doctorId, LocalDate fechaInicio, LocalDate fechaFin);
    
    /**
     * Obtiene los horarios disponibles para un doctor en una fecha específica
     * @param doctorId ID del doctor
     * @param fecha Fecha para la que se quieren obtener los horarios
     * @return Lista de horarios disponibles
     */
    List<HorarioDisponibleDTO> obtenerHorariosDisponibles(String doctorId, LocalDate fecha);

    boolean validarDisponibilidadDoctor(String doctorId, LocalDate fecha, LocalTime hora);
} 