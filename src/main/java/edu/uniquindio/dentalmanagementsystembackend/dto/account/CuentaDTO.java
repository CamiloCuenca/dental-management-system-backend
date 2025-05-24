package edu.uniquindio.dentalmanagementsystembackend.dto.account;

import java.time.LocalDate;

public record CuentaDTO(
    Long id,
    String identificacion,
    String nombres,
    String apellidos,
    String direccion,
    LocalDate fechaNacimiento,
    String telefono,
    String correoElectronico
) {
}