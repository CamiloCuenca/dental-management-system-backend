package edu.uniquindio.dentalmanagementsystembackend.Enum;

import lombok.Getter;

@Getter
public enum EstadoDisponibilidad {
    ACTIVO("Disponible para atención", "ACT"),
    VACACIONES("En período de vacaciones", "VAC"),
    PERMISO("Permiso temporal", "PER"),
    INCAPACIDAD("Incapacidad médica", "INC"),
    CAPACITACION("En capacitación", "CAP");

    private final String descripcion;
    private final String codigo;

    EstadoDisponibilidad(String descripcion, String codigo) {
        this.descripcion = descripcion;
        this.codigo = codigo;
    }

}