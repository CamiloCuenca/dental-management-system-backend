package edu.uniquindio.dentalmanagementsystembackend.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PerfilDTO(

         String idNumber,
         String name,
         String lastName,
         String phoneNumber,
         String address,
         LocalDate fechaNacimiento,
         String email

) {
}
