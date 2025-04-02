package edu.uniquindio.dentalmanagementsystembackend.dto.account;

public record DoctorDTO(
        String idNumber,
        String name,
        String lastName,
        String email,
        String tipoDoctor  // A
) {}