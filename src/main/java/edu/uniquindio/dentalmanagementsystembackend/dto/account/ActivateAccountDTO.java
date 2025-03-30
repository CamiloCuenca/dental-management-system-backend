package edu.uniquindio.dentalmanagementsystembackend.dto.account;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ActivateAccountDTO(

        @NotBlank(message = "El código de activación no puede estar vacío.")
        @Size(min = 6, max = 20, message = "El código de activación debe tener entre 6 y 20 caracteres.")
        String code,

        @NotBlank(message = "El email no puede estar vacío.")
        @Email(message = "El email no tiene un formato válido.")
        String email

) {
}
