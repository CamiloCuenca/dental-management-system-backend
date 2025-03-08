package edu.uniquindio.dentalmanagementsystembackend.dto.JWT;

public record MensajeDTO <T>(
        boolean error,
        T respuesta

) {
}
