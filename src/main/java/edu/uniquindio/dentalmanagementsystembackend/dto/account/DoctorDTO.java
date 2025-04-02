package edu.uniquindio.dentalmanagementsystembackend.dto;

public record DoctorDTO(
        String idNumber,
        String name,
        String lastName,
        String email,
        String tipoDoctor  // A
) {}