package edu.uniquindio.dentalmanagementsystembackend.service.Interfaces;

import edu.uniquindio.dentalmanagementsystembackend.entity.Inventario;

import java.util.List;

public interface ServiciosInventario {
    
    /**
     * Obtiene todos los elementos del inventario
     * @return Lista de elementos del inventario
     */
    List<Inventario> listarInventario();
    
    /**
     * Obtiene un elemento del inventario por su ID
     * @param id ID del elemento
     * @return Elemento encontrado
     */
    Inventario obtenerElementoPorId(Long id);
    
    /**
     * Crea un nuevo elemento en el inventario
     * @param elemento Elemento a crear
     * @return Elemento creado
     */
    Inventario crearElemento(Inventario elemento);
    
    /**
     * Actualiza un elemento existente en el inventario
     * @param elemento Elemento a actualizar
     * @return Elemento actualizado
     */
    Inventario actualizarElemento(Inventario elemento);
    
    /**
     * Elimina un elemento del inventario por su ID
     * @param id ID del elemento a eliminar
     */
    void eliminarElemento(Long id);
    
    /**
     * Actualiza la cantidad de un elemento en el inventario
     * @param id ID del elemento
     * @param cantidad Nueva cantidad
     * @return Elemento actualizado
     */
    Inventario actualizarCantidad(Long id, int cantidad);
} 