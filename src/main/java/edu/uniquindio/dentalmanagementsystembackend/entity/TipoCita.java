package edu.uniquindio.dentalmanagementsystembackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "tipos_cita", indexes = {
    @Index(name = "idx_tipo_cita_nombre", columnList = "nombre", unique = true)
})
public class TipoCita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del tipo de cita es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    @Column(nullable = false, unique = true)
    private String nombre;

    @Size(max = 500, message = "La descripción no puede exceder los 500 caracteres")
    @Column(length = 500)
    private String descripcion;

    @NotNull(message = "La especialidad es obligatoria")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "especialidad_id", nullable = false)
    private Especialidad especialidadRequerida;

    @NotNull(message = "La duración de la cita es obligatoria")
    @Min(value = 15, message = "La duración mínima debe ser de 15 minutos")
    @Column(name = "duracion_minutos", nullable = false)
    private Integer duracionMinutos;

    @Column(nullable = false)
    private Boolean requiereHistorial = false;

    @Min(value = 0, message = "La prioridad no puede ser negativa")
    @Column(name = "prioridad", nullable = false)
    private Integer prioridad = 0;

    @NotBlank(message = "El código interno es obligatorio")
    @Size(max = 10, message = "El código interno no puede exceder los 10 caracteres")
    @Column(name = "codigo_interno", unique = true, nullable = false, length = 10)
    private String codigoInterno;

    @Column(nullable = false)
    private Boolean activo = true;

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
    private void validarDatos() {
        if (duracionMinutos != null && duracionMinutos > 480) {
            throw new IllegalArgumentException("La duración no puede superar 480 minutos (8 horas)");
        }

        if (prioridad != null && prioridad > 100) {
            throw new IllegalArgumentException("La prioridad no puede ser mayor a 100");
        }
    }
}