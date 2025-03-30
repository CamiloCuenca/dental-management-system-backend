package edu.uniquindio.dentalmanagementsystembackend.dto.account;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ChangePasswordCodeDTO(
        @NotBlank(message = "El código de activación no puede estar vacío.")
        @Size(min = 6, max = 20, message = "El código de activación debe tener entre 6 y 20 caracteres.")
        String code,
        @NotBlank(message = "La contraseña no puede estar vacía.")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres.")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "La contraseña debe tener al menos una mayúscula, una minúscula, un número y un carácter especial."
        )
        String newPassword,
        @NotBlank(message = "La contraseña no puede estar vacía.")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres.")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "La contraseña debe tener al menos una mayúscula, una minúscula, un número y un carácter especial."
        )
        String confirmationPassword


) {
}
