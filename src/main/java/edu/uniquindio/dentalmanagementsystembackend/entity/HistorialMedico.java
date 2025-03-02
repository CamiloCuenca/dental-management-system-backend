package edu.uniquindio.dentalmanagementsystembackend.entity;

import edu.uniquindio.dentalmanagementsystembackend.entity.Account.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "historiales_medicos")
public class HistorialMedico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    // Relación con User para almacenar información del paciente
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.SET_NULL) // Evita eliminación en cascada
    @JoinColumn(name = "paciente_id", nullable = false)
    private User paciente;

    // Relación con User para almacenar información del odontólogo
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.SET_NULL) // Evita eliminación en cascada
    @JoinColumn(name = "odontologo_id", nullable = false)
    private User odontologo;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Lob
    @Column(name = "descripcion", nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    public HistorialMedico(User paciente, User odontologo, LocalDate fecha, String descripcion) {
        this.paciente = paciente;
        this.odontologo = odontologo;
        this.fecha = fecha;
        this.descripcion = descripcion;
    }
}