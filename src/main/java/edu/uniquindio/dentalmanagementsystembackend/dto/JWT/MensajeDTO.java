package edu.uniquindio.dentalmanagementsystembackend.dto.JWT;

import jakarta.validation.constraints.NotNull;

public record MensajeDTO <T>(
        @NotNull(message = "El campo error no puede ser nulo.")
        boolean error,
        
        @NotNull(message = "La respuesta no puede ser nula.")
        T respuesta
) {
}
