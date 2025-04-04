package edu.uniquindio.dentalmanagementsystembackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "especialidades", indexes = {
    @Index(name = "idx_especialidad_nombre", columnList = "nombre", unique = true)
})
public class Especialidad {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El nombre de la especialidad es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    @Column(nullable = false, unique = true)
    private String nombre;
    
    @Size(max = 500, message = "La descripción no puede exceder los 500 caracteres")
    @Column(length = 500)
    private String descripcion;
    
    @Min(value = 15, message = "La duración mínima debe ser de 15 minutos")
    @Column(nullable = false, name = "duracion_promedio")
    private Integer duracionPromedioMinutos;
    
    @Column(nullable = false)
    private Boolean activo = true;
    
    @Column(name = "codigo_interno", unique = true, length = 10)
    private String codigoInterno;
    
    @Min(value = 0)
    @Column(name = "nivel_complejidad")
    private Integer nivelComplejidad;
    
    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    
    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    @Version
    private Long version;
} 