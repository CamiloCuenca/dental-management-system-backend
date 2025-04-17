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

    /**
     * Valida si un doctor está disponible en una fecha y hora específicos.
     *
     * @param doctorId Identificador único del doctor cuya disponibilidad se quiere verificar.
     * @param fecha Fecha para la cual se desea comprobar la disponibilidad.
     * @param hora Hora para la cual se desea comprobar la disponibilidad.
     * @return true si el doctor está disponible en la fecha y hora especificadas, false en caso contrario.
     */
    boolean validarDisponibilidadDoctor(String doctorId, LocalDate fecha, LocalTime hora);
} 