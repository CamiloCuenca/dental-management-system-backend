package edu.uniquindio.dentalmanagementsystembackend.dto.account;

public record ChangePasswordCodeDTO(
        String code,
        String newPassword,
        String confirmationPassword


) {
}
