package edu.uniquindio.dentalmanagementsystembackend.dto.account;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

public record ActualizarUsuarioDTO(
        @NotBlank(message = "El nombre no puede estar vacío.")
        @Size(max = 50, message = "El nombre no puede tener más de 50 caracteres.")
        @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúñÑ ]+$", message = "El nombre solo puede contener letras y espacios.")
        String name,
        
        @NotBlank(message = "El apellido no puede estar vacío.")
        @Size(max = 50, message = "El apellido no puede tener más de 50 caracteres.")
        @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúñÑ ]+$", message = "El apellido solo puede contener letras y espacios.")
        String lastName,
        
        @NotBlank(message = "El número de teléfono no puede estar vacío.")
        @Pattern(regexp = "^\\d{10}$", message = "El número de teléfono debe tener exactamente 10 dígitos.")
        String phoneNumber,
        
        @NotBlank(message = "La dirección no puede estar vacía.")
        @Size(max = 100, message = "La dirección no puede tener más de 100 caracteres.")
        String address,
        
        @NotBlank(message = "El email no puede estar vacío.")
        @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "El email no tiene un formato válido.")
        String email
) {}