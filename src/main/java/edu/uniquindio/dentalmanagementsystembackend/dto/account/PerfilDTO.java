package edu.uniquindio.dentalmanagementsystembackend.dto.account;

import java.time.LocalDate;

public record PerfilDTO(
        String idNumber,
        String name,
        String lastName,
        String phoneNumber,
        String address,
        LocalDate birthDate,
        String email

) {
}