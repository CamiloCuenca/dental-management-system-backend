package edu.uniquindio.dentalmanagementsystembackend.dto.Inventario;

import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoInventario;
import edu.uniquindio.dentalmanagementsystembackend.Enum.TipoProducto;

public record InventarioDetalleDTO(
        Long id,
        String nombre,
        String descripcion,
        TipoProducto tipoProducto,
        Integer cantidadDisponible,
        Integer cantidadMinima,
        EstadoInventario estado
) {
}
