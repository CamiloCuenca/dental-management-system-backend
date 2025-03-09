package edu.uniquindio.dentalmanagementsystembackend.dto.account;

public record UpdatePasswordDTO(
        String currentPassword,
        String newPassword,
        String confirmationPassword
) {
}
