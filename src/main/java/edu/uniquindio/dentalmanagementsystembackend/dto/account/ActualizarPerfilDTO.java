package edu.uniquindio.dentalmanagementsystembackend.dto.account;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ActualizarPerfilDTO(
        @NotBlank(message = "El nombre no puede estar vacío.")
        @Size(max = 50, message = "El nombre no puede tener más de 50 caracteres.")
        @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúñÑ ]+$", message = "El nombre solo puede contener letras y espacios.")
        String name,
        @NotBlank(message = "El nombre no puede estar vacío.")
        @Size(max = 50, message = "El nombre no puede tener más de 50 caracteres.")
        @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúñÑ ]+$", message = "El nombre solo puede contener letras y espacios.")
        String lastName,
        @NotBlank(message = "El número de teléfono no puede estar vacío.")
        @Pattern(regexp = "^\\d{10}$", message = "El número de teléfono debe tener exactamente 10 dígitos.")
        String phoneNumber,
        @NotBlank(message = "La dirección no puede estar vacía.")
        @Size(max = 100, message = "La dirección no puede tener más de 100 caracteres.")
        String address
) {
}
