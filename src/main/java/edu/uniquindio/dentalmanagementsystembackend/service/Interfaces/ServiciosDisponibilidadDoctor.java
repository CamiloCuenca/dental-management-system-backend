package edu.uniquindio.dentalmanagementsystembackend.service.Interfaces;

import edu.uniquindio.dentalmanagementsystembackend.entity.DisponibilidadDoctor;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.User;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

public interface ServiciosDisponibilidadDoctor {
    
    /**
     * Obtiene todas las disponibilidades de doctores
     * @return Lista de disponibilidades
     */
    List<DisponibilidadDoctor> listarDisponibilidades();
    
    /**
     * Obtiene una disponibilidad por su ID
     * @param id ID de la disponibilidad
     * @return Disponibilidad encontrada
     */
    DisponibilidadDoctor obtenerDisponibilidadPorId(Long id);
    
    /**
     * Obtiene todas las disponibilidades de un doctor específico
     * @param doctorId ID del doctor
     * @return Lista de disponibilidades del doctor
     */
    List<DisponibilidadDoctor> obtenerDisponibilidadesPorDoctor(String doctorId);
    
    /**
     * Verifica si un doctor está disponible en un día y hora específicos
     * @param doctorId ID del doctor
     * @param diaSemana Día de la semana
     * @param hora Hora a verificar
     * @return true si está disponible, false en caso contrario
     */
    boolean verificarDisponibilidad(String doctorId, DayOfWeek diaSemana, LocalTime hora);
    
    /**
     * Crea una nueva disponibilidad para un doctor
     * @param disponibilidad Disponibilidad a crear
     * @return Disponibilidad creada
     */
    DisponibilidadDoctor crearDisponibilidad(DisponibilidadDoctor disponibilidad);
    
    /**
     * Actualiza una disponibilidad existente
     * @param disponibilidad Disponibilidad a actualizar
     * @return Disponibilidad actualizada
     */
    DisponibilidadDoctor actualizarDisponibilidad(DisponibilidadDoctor disponibilidad);
    
    /**
     * Elimina una disponibilidad por su ID
     * @param id ID de la disponibilidad a eliminar
     */
    void eliminarDisponibilidad(Long id);
} 