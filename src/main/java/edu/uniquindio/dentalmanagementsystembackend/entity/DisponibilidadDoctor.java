package edu.uniquindio.dentalmanagementsystembackend.entity;

import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoDisponibilidad;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "disponibilidad_doctor", indexes = {
    @Index(name = "idx_disponibilidad_doctor", columnList = "doctor_id"),
    @Index(name = "idx_disponibilidad_fecha", columnList = "dia_semana")
})
public class DisponibilidadDoctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El doctor es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;

    @NotNull(message = "El día de la semana es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false, length = 15)
    private DayOfWeek diaSemana;

    @NotNull(message = "La hora de inicio es obligatoria")
    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @NotNull(message = "La hora de fin es obligatoria")
    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @NotNull(message = "El estado es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private EstadoDisponibilidad estado = EstadoDisponibilidad.ACTIVO;

    @Column(name = "fecha_inicio_excepcion")
    private LocalDateTime fechaInicioExcepcion;

    @Column(name = "fecha_fin_excepcion")
    private LocalDateTime fechaFinExcepcion;

    @Column(name = "motivo_excepcion", length = 255)
    private String motivoExcepcion;

    @Column(name = "intervalo_citas")
    private Integer intervaloCitasMinutos = 30;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Version
    private Long version;

    @PrePersist
    @PreUpdate
    private void validarHorario() {
        if (horaInicio != null && horaFin != null && horaInicio.isAfter(horaFin)) {
            throw new IllegalArgumentException("La hora de inicio debe ser anterior a la hora de fin");
        }

        if (intervaloCitasMinutos != null && intervaloCitasMinutos <= 0) {
            throw new IllegalArgumentException("El intervalo de citas debe ser mayor que cero");
        }

        if (fechaInicioExcepcion != null && fechaFinExcepcion != null &&
                fechaInicioExcepcion.isAfter(fechaFinExcepcion)) {
            throw new IllegalArgumentException("La fecha de inicio de la excepción no puede ser posterior a la de fin");
        }
    }
}
