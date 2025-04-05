package edu.uniquindio.dentalmanagementsystembackend.entity.Account;

import edu.uniquindio.dentalmanagementsystembackend.entity.Cita;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa el historial médico de un paciente.
 * Esta clase almacena toda la información relacionada con las consultas y tratamientos
 * realizados a un paciente por un odontólogo específico.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"paciente", "odontologo", "cita"})
@Entity
@Table(name = "historiales_medicos")
public class HistorialMedico {

    /**
     * Identificador único del historial médico.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * Paciente al que pertenece el historial.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "paciente_id", referencedColumnName = "id_number", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User paciente;

    /**
     * Odontólogo que creó el historial.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "odontologo_id", referencedColumnName = "id_number", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User odontologo;

    /**
     * Cita asociada al historial.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cita_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
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
     * Procedimientos realizados en la consulta
     */
    @ElementCollection
    @CollectionTable(name = "procedimientos_historial", joinColumns = @JoinColumn(name = "historial_id"))
    @Column(name = "procedimiento", nullable = false)
    private List<String> procedimientos = new ArrayList<>();

    /**
     * Constructor con todos los campos necesarios para crear un historial médico.
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
        this.procedimientos = new ArrayList<>();
    }

    /**
     * Agrega un procedimiento al historial.
     *
     * @param procedimiento Procedimiento a agregar
     */
    public void agregarProcedimiento(String procedimiento) {
        this.procedimientos.add(procedimiento);
    }
}