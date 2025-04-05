package edu.uniquindio.dentalmanagementsystembackend.dto.account;


public record ActualizarUsuarioDTO(
        String name,
        String lastName,
        String phoneNumber,
        String address,
        String email

) {
}