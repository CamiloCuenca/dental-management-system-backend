package edu.uniquindio.dentalmanagementsystembackend.dto.historial;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ActualizarHistorial(

        /**
         * Diagnóstico realizado por el odontólogo.
         */
        @NotBlank(message = "El diagnóstico no puede estar vacío")
        @Size(max = 1000, message = "El diagnóstico no puede exceder 1000 caracteres")
        String diagnostico,

        /**
         * Tratamiento prescrito por el odontólogo.
         */
        @NotBlank(message = "El tratamiento no puede estar vacío")
        @Size(max = 1000, message = "El tratamiento no puede exceder 1000 caracteres")
        String tratamiento,

        /**
         * Observaciones adicionales del odontólogo.
         */
        @Size(max = 500, message = "Las observaciones no pueden exceder 500 caracteres")
        String observaciones

) {
}
