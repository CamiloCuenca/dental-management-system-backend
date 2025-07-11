package edu.uniquindio.dentalmanagementsystembackend.dto.account;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * DTO para el registro de cuentas con verificación de reCAPTCHA v3.
 */
public record RegistroConCaptchaDTO(
        @NotBlank(message = "El número de identificación no puede estar vacío.")
        @Size(min = 5, max = 15, message = "El número de identificación debe tener entre 5 y 15 dígitos.")
        @Pattern(regexp = "^\\d{5,15}$", message = "El número de identificación solo puede contener entre 5 y 15 dígitos numéricos.")
        String idNumber,

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

        @NotNull(message = "La fecha de nacimiento no puede estar vacía.")
        @Past(message = "La fecha de nacimiento debe ser en el pasado.")
        LocalDate fechaNacimiento,

        @NotBlank(message = "El email no puede estar vacío.")
        @Email(message = "El email no tiene un formato válido.")
        String email,

        @NotBlank(message = "La contraseña no puede estar vacía.")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres.")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "La contraseña debe tener al menos una mayúscula, una minúscula, un número y un carácter especial."
        )
        String password,

        @NotBlank(message = "El token de reCAPTCHA es requerido")
        String captchaToken
) {
    
    /**
     * Convierte este DTO a CrearCuentaDTO para usar con el servicio.
     */
    public CrearCuentaDTO toCrearCuentaDTO() {
        return new CrearCuentaDTO(
                idNumber,
                name,
                lastName,
                phoneNumber,
                address,
                fechaNacimiento,
                email,
                password
        );
    }
} 