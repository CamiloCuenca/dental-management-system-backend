package edu.uniquindio.dentalmanagementsystembackend.dto.JWT;

import jakarta.validation.constraints.NotBlank;

public record TokenDTO(
        @NotBlank(message = "El token no puede estar vacío.")
        String token
) {
}
