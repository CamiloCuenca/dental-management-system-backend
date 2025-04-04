package edu.uniquindio.dentalmanagementsystembackend.service.Interfaces;

import edu.uniquindio.dentalmanagementsystembackend.entity.Especialidad;

import java.util.List;

public interface ServiciosEspecialidad {
    
    /**
     * Obtiene todas las especialidades
     * @return Lista de especialidades
     */
    List<Especialidad> listarEspecialidades();
    
    /**
     * Obtiene una especialidad por su ID
     * @param id ID de la especialidad
     * @return Especialidad encontrada
     */
    Especialidad obtenerEspecialidadPorId(Long id);
    
    /**
     * Crea una nueva especialidad
     * @param especialidad Especialidad a crear
     * @return Especialidad creada
     */
    Especialidad crearEspecialidad(Especialidad especialidad);
    
    /**
     * Actualiza una especialidad existente
     * @param especialidad Especialidad a actualizar
     * @return Especialidad actualizada
     */
    Especialidad actualizarEspecialidad(Especialidad especialidad);
    
    /**
     * Elimina una especialidad por su ID
     * @param id ID de la especialidad a eliminar
     */
    void eliminarEspecialidad(Long id);
} 