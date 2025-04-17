package edu.uniquindio.dentalmanagementsystembackend.dto.account;

/**
 * DTO para la transferencia de datos de correo electrónico
 */
public record EmailDTO(

        String recipient,
        String issue,
        String body

) {
}
