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
    // Relación con User para obtener detalles del paciente
    @ManyToOne(fetch = FetchType.LAZY, optional = true) // Permitir null para historial de citas
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "paciente_id", nullable = true)
    private User paciente;

    // Relación con User para obtener detalles del odontólogo
    @ManyToOne(fetch = FetchType.LAZY, optional = true) // Permitir null para historial de citas
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "odontologo_id", nullable = true)
    private User odontologo;

    @Column(name = "fecha_hora", nullable = false)
    private Instant fechaHora;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoCitas estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cita", nullable = false) // Corrección del nombre
    private TipoCita tipoCita;

    public Cita(User paciente, User odontologo, Instant fechaHora, EstadoCitas estado , TipoCita tipoCita) {
        this.paciente = paciente;
        this.odontologo = odontologo;
        this.fechaHora = fechaHora;
        this.estado = estado;
        this.tipoCita = tipoCita;
    }
}