package edu.uniquindio.dentalmanagementsystembackend.entity.Account;

import edu.uniquindio.dentalmanagementsystembackend.entity.Cita;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.time.LocalDate;

/**
 * Entidad que representa el historial médico de un paciente.
 * Esta clase almacena toda la información relacionada con las consultas y tratamientos
 * realizados a un paciente por un odontólogo específico.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "historiales_medicos")
public class HistorialMedico {

    /**
     * Identificador único del historial médico.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Paciente al que pertenece el historial.
     * Relación ManyToOne con la entidad User.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", referencedColumnName = "id_number", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ToString.Exclude
    private User paciente;

    /**
     * Odontólogo que creó el historial.
     * Relación ManyToOne con la entidad User.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "odontologo_id", referencedColumnName = "id_number", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ToString.Exclude
    private User odontologo;

    /**
     * Cita asociada al historial.
     * Relación ManyToOne con la entidad Cita.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cita_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ToString.Exclude
    private Cita cita;

    /**
     * Fecha en que se realizó la consulta.
     */
    @Column(nullable = false)
    private LocalDate fecha;

    /**
     * Diagnóstico realizado por el odontólogo.
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String diagnostico;

    /**
     * Tratamiento prescrito por el odontólogo.
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String tratamiento;

    /**
     * Observaciones adicionales del odontólogo.
     */
    @Column(columnDefinition = "TEXT")
    private String observaciones;

    /**
     * Fecha programada para la próxima cita.
     */
    private LocalDate proximaCita;

    /**
     * Constructor con todos los campos necesarios para crear un historial médico.
     *
     * @param paciente Paciente al que pertenece el historial
     * @param odontologo Odontólogo que creó el historial
     * @param cita Cita asociada al historial
     * @param fecha Fecha de la consulta
     * @param diagnostico Diagnóstico realizado
     * @param tratamiento Tratamiento prescrito
     * @param observaciones Observaciones adicionales
     * @param proximaCita Fecha de la próxima cita
     */
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