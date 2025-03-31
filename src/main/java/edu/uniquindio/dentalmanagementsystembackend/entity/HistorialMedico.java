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
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", referencedColumnName = "id_number", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "odontologo_id", referencedColumnName = "id_number", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User odontologo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cita_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Cita cita;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String diagnostico;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String tratamiento;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    private LocalDate proximaCita;

    public HistorialMedico(User paciente, User odontologo, Cita cita, LocalDate fecha, 
                          String diagnostico, String tratamiento, String observaciones, 
                          LocalDate proximaCita) {
        this.paciente = paciente;
        this.odontologo = odontologo;
        this.cita = cita;
        this.fecha = fecha;
        this.diagnostico = diagnostico;
        this.tratamiento = tratamiento;
        this.observaciones = observaciones;
        this.proximaCita = proximaCita;
    }
}