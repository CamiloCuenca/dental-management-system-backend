package edu.uniquindio.dentalmanagementsystembackend.entity;

import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoCitas;
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
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"paciente", "doctor", "tipoCita"})
@Entity
@Table(name = "citas")
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private User paciente;

    @Column(name = "es_autenticada", nullable = false)
    private boolean esAutenticada;

    // Campos para paciente no autenticado
    @Column(name = "nombre_paciente_no_autenticado")
    private String nombrePacienteNoAutenticado;

    @Column(name = "numero_identificacion_no_autenticado")
    private String numeroIdentificacionNoAutenticado;

    @Column(name = "telefono_no_autenticado")
    private String telefonoNoAutenticado;

    @Column(name = "email_no_autenticado")
    private String emailNoAutenticado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private User doctor;

    @Column(name = "fecha_hora", nullable = false)
    private Instant fechaHora;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoCitas estado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo_cita_id", nullable = false)
    private TipoCita tipoCita;

    public Cita(User paciente, User doctor, Instant fechaHora, EstadoCitas estado, TipoCita tipoCita) {
        this.paciente = paciente;
        this.doctor = doctor;
        this.fechaHora = fechaHora;
        this.estado = estado;
        this.tipoCita = tipoCita;
        this.esAutenticada = true;
    }

    public Cita(String nombrePacienteNoAutenticado, String numeroIdentificacionNoAutenticado,
                String telefonoNoAutenticado, String emailNoAutenticado, User doctor,
                Instant fechaHora, EstadoCitas estado, TipoCita tipoCita) {
        this.nombrePacienteNoAutenticado = nombrePacienteNoAutenticado;
        this.numeroIdentificacionNoAutenticado = numeroIdentificacionNoAutenticado;
        this.telefonoNoAutenticado = telefonoNoAutenticado;
        this.emailNoAutenticado = emailNoAutenticado;
        this.doctor = doctor;
        this.fechaHora = fechaHora;
        this.estado = estado;
        this.tipoCita = tipoCita;
        this.esAutenticada = false;
    }
}
