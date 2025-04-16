package edu.uniquindio.dentalmanagementsystembackend.dto.historial;

public record ActualizarHistorial(

        /**
         * Diagnóstico realizado por el odontólogo.
         */
        String diagnostico,

        /**
         * Tratamiento prescrito por el odontólogo.
         */
        String tratamiento,

        /**
         * Observaciones adicionales del odontólogo.
         */
        String observaciones

) {
}
