package edu.uniquindio.dentalmanagementsystembackend.dto.account;

import java.time.LocalDate;

public record ActualizarUsuarioDTO(
        String name,
        String lastName,
        String phoneNumber,
        String address,
        String email

) {
}