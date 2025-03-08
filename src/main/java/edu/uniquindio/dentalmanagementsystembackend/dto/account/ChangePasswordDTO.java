package edu.uniquindio.dentalmanagementsystembackend.dto.account;

public record ChangePasswordDTO(
        String code,
        String newPassword,
        String confirmationPassword


) {
}
