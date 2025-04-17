package edu.uniquindio.dentalmanagementsystembackend.dto.account;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginDTO(
        @NotBlank(message = "El número de identificación no puede estar vacío.")
        @Size(min = 5, max = 15, message = "El número de identificación debe tener entre 5 y 15 dígitos.")
        @Pattern(regexp = "^\\d{5,15}$", message = "El número de identificación solo puede contener entre 5 y 15 dígitos numéricos.")
        String idNumber,

        @NotBlank(message = "La contraseña no puede estar vacía.")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres.")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "La contraseña debe tener al menos una mayúscula, una minúscula, un número y un carácter especial."
        )
        String password
) {
}
