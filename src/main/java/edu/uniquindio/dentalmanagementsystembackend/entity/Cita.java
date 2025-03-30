package edu.uniquindio.dentalmanagementsystembackend.entity;

import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoCitas;
import edu.uniquindio.dentalmanagementsystembackend.Enum.TipoCita;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "citas")
public class Cita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "paciente_id", nullable = true)
    private User paciente;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "odontologo_id", nullable = true)
    private User odontologo;

    @Column(name = "fecha_hora", nullable = false)
    private Instant fechaHora;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoCitas estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cita", nullable = false)
    private TipoCita tipoCita;

    public Cita(User paciente, User odontologo, Instant fechaHora, EstadoCitas estado, TipoCita tipoCita) {
        validarOdontologo(odontologo, tipoCita);
        this.paciente = paciente;
        this.odontologo = odontologo;
        this.fechaHora = fechaHora;
        this.estado = estado;
        this.tipoCita = tipoCita;
    }

    private void validarOdontologo(User odontologo, TipoCita tipoCita) {
        if (odontologo != null && odontologo.getAccount().getTipoDoctor() != tipoCita.getTipoDoctorRequerido()) {
            throw new IllegalArgumentException("El odontólogo seleccionado no tiene la especialidad requerida para esta cita.");
        }
    }
}