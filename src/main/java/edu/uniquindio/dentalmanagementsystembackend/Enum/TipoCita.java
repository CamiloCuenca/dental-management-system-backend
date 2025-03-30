package edu.uniquindio.dentalmanagementsystembackend.Enum;

import lombok.Getter;

@Getter
public enum TipoCita {
    CONSULTA_GENERAL(TipoDoctor.ODONTOLOGO_GENERAL),
    LIMPIEZA_DENTAL(TipoDoctor.HIGIENISTA_DENTAL),
    EXTRACCION_DIENTES(TipoDoctor.CIRUJANO_ORAL_Y_MAXILOFACIAL),
    TRATAMIENTO_DE_CONDUCTO(TipoDoctor.ENDODONCISTA),
    ORTODONCIA(TipoDoctor.ORTODONCISTA),
    IMPLANTES_DENTALES(TipoDoctor.PERIODONCISTA),
    BLANQUEAMIENTO_DENTAL(TipoDoctor.ODONTOLOGO_ESTETICO),
    OTRO(null);

    private final TipoDoctor tipoDoctorRequerido;

    TipoCita(TipoDoctor tipoDoctorRequerido) {
        this.tipoDoctorRequerido = tipoDoctorRequerido;
    }

}
