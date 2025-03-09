package edu.uniquindio.dentalmanagementsystembackend.dto.account;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CrearCuentaDTO(

        String idNumber,

        String name,

        String lastName,

        String phoneNumber,

        String address,

        LocalDate fechaNacimiento, // Cambio de LocalDateTime a LocalDate

        String email,

        String password
) {






}
