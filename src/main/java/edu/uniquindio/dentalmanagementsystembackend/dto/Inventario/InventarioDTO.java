package edu.uniquindio.dentalmanagementsystembackend.dto.Inventario;

import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoInventario;
import edu.uniquindio.dentalmanagementsystembackend.Enum.TipoProducto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.DecimalMin;

import java.time.LocalDate;

public record InventarioDTO(
        @NotBlank(message = "El nombre del producto no puede estar vacío.")
        @Size(max = 100, message = "El nombre del producto no puede exceder 100 caracteres.")
        String nombre,
        
        @NotBlank(message = "La descripción no puede estar vacía.")
        @Size(max = 500, message = "La descripción no puede exceder 500 caracteres.")
        String descripcion,
        
        @NotNull(message = "El tipo de producto no puede ser nulo.")
        TipoProducto tipoProducto,
        
        @NotNull(message = "La cantidad disponible no puede ser nula.")
        @Min(value = 0, message = "La cantidad disponible no puede ser negativa.")
        Integer cantidadDisponible,
        
        @NotNull(message = "La cantidad mínima no puede ser nula.")
        @Min(value = 0, message = "La cantidad mínima no puede ser negativa.")
        Integer cantidadMinima,
        
        @NotNull(message = "El precio unitario no puede ser nulo.")
        @DecimalMin(value = "0.0", inclusive = false, message = "El precio unitario debe ser mayor a 0.")
        Double precioUnitario,
        
        LocalDate fechaVencimiento,
        
        @NotNull(message = "El estado del inventario no puede ser nulo.")
        EstadoInventario estado,
        
        @Size(max = 100, message = "La ubicación no puede exceder 100 caracteres.")
        String ubicacion,
        
        Boolean esSterilizable,
        
        @Min(value = 0, message = "La vida útil de esterilización no puede ser negativa.")
        Integer vidaUtilSterilizacion
) {
}
