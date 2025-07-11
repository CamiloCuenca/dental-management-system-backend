package edu.uniquindio.dentalmanagementsystembackend.dto.account;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para la transferencia de datos de correo electrónico
 */
public record EmailDTO(
        @NotBlank(message = "El destinatario no puede estar vacío.")
        @Email(message = "El formato del email no es válido.")
        String recipient,

        @NotBlank(message = "El asunto no puede estar vacío.")
        @Size(max = 200, message = "El asunto no puede exceder 200 caracteres.")
        String issue,

        @NotBlank(message = "El cuerpo del email no puede estar vacío.")
        @Size(max = 2000, message = "El cuerpo del email no puede exceder 2000 caracteres.")
        String body
) {

    public String email() {
        return recipient;
    }
}
