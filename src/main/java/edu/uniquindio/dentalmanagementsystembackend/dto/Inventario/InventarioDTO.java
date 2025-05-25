package edu.uniquindio.dentalmanagementsystembackend.dto.Inventario;
import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoInventario;
import edu.uniquindio.dentalmanagementsystembackend.Enum.TipoProducto;

import java.time.LocalDate;

public record InventarioDTO(
        String nombre,
        String descripcion,
        TipoProducto tipoProducto,
        Integer cantidadDisponible,
        Integer cantidadMinima,
        Double precioUnitario,
        LocalDate fechaVencimiento,
        Boolean esSterilizable,
        Integer vidaUtilSterilizacion
) {
}
