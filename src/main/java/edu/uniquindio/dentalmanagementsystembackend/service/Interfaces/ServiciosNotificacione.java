package edu.uniquindio.dentalmanagementsystembackend.service.Interfaces;

import edu.uniquindio.dentalmanagementsystembackend.entity.Notificacione;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.User;

import java.util.List;

public interface ServiciosNotificacione {
    
    /**
     * Obtiene todas las notificaciones
     * @return Lista de notificaciones
     */
    List<Notificacione> listarNotificaciones();
    
    /**
     * Obtiene una notificación por su ID
     * @param id ID de la notificación
     * @return Notificación encontrada
     */
    Notificacione obtenerNotificacionPorId(Long id);
    
    /**
     * Obtiene todas las notificaciones de un usuario específico
     * @param userId ID del usuario
     * @return Lista de notificaciones del usuario
     */
    List<Notificacione> obtenerNotificacionesPorUsuario(Long userId);
    
    /**
     * Crea una nueva notificación
     * @param notificacione Notificación a crear
     * @return Notificación creada
     */
    Notificacione crearNotificacion(Notificacione notificacione);
    
    /**
     * Actualiza una notificación existente
     * @param notificacione Notificación a actualizar
     * @return Notificación actualizada
     */
    Notificacione actualizarNotificacion(Notificacione notificacione);
    
    /**
     * Elimina una notificación por su ID
     * @param id ID de la notificación a eliminar
     */
    void eliminarNotificacion(Long id);
    
    /**
     * Marca una notificación como leída
     * @param id ID de la notificación
     * @return Notificación actualizada
     */
    Notificacione marcarComoLeida(Long id);
} 