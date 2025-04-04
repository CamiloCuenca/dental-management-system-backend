package edu.uniquindio.dentalmanagementsystembackend.service.Interfaces;

import edu.uniquindio.dentalmanagementsystembackend.entity.TipoCita;

import java.util.List;

public interface ServiciosTipoCita {
    
    /**
     * Obtiene todos los tipos de cita disponibles
     * @return Lista de tipos de cita
     */
    List<TipoCita> listarTiposCita();
    
    /**
     * Obtiene un tipo de cita por su ID
     * @param id ID del tipo de cita
     * @return Tipo de cita encontrado
     */
    TipoCita obtenerTipoCitaPorId(Long id);
    
    /**
     * Crea un nuevo tipo de cita
     * @param tipoCita Tipo de cita a crear
     * @return Tipo de cita creado
     */
    TipoCita crearTipoCita(TipoCita tipoCita);
    
    /**
     * Actualiza un tipo de cita existente
     * @param tipoCita Tipo de cita a actualizar
     * @return Tipo de cita actualizado
     */
    TipoCita actualizarTipoCita(TipoCita tipoCita);
    
    /**
     * Elimina un tipo de cita por su ID
     * @param id ID del tipo de cita a eliminar
     */
    void eliminarTipoCita(Long id);
} 