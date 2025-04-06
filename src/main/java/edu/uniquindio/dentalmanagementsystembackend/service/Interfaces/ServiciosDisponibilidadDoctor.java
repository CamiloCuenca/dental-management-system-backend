package edu.uniquindio.dentalmanagementsystembackend.service.Interfaces;

import edu.uniquindio.dentalmanagementsystembackend.entity.DisponibilidadDoctor;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.User;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ServiciosDisponibilidadDoctor {
    
    /**
     * Obtiene las fechas disponibles para un doctor en un rango de fechas
     * @param doctorId ID del doctor
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha de fin del rango
     * @return Lista de fechas disponibles
     */
    List<LocalDate> obtenerFechasDisponibles(String doctorId, LocalDate fechaInicio, LocalDate fechaFin);
    
    /**
     * Obtiene los horarios disponibles para un doctor en una fecha específica
     * @param doctorId ID del doctor
     * @param fecha Fecha para la que se quieren obtener los horarios
     * @return Lista de horarios disponibles
     */
    List<LocalTime> obtenerHorariosDisponibles(String doctorId, LocalDate fecha);
} 