package edu.uniquindio.dentalmanagementsystembackend.entity.Account;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ValidationCode {

    private LocalDateTime creationDate;
    private String code;

    public ValidationCode(String code) {
        this.code = code;
        this.creationDate = LocalDateTime.now();  // Fecha de creación al generar el código
    }

    public boolean isExpired() {
        // Verifica si han pasado más de 15 minutos desde la creación
        return creationDate.plusMinutes(15).isBefore(LocalDateTime.now());
    }
}
