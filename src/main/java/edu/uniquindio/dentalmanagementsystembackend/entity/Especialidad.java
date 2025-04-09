package edu.uniquindio.dentalmanagementsystembackend.entity;

import edu.uniquindio.dentalmanagementsystembackend.entity.Account.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"doctores", "tiposCita"})
@EqualsAndHashCode(of = "id", exclude = {"doctores", "tiposCita"})
@Table(name = "especialidades", indexes = {
    @Index(name = "idx_especialidad_nombre", columnList = "nombre", unique = true)
})
public class Especialidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de la especialidad es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Size(max = 500, message = "La descripción no puede exceder los 500 caracteres")
    @Column(length = 500)
    private String descripcion;

    @NotBlank(message = "El código interno es obligatorio")
    @Column(name = "codigo_interno", unique = true, length = 10, nullable = false)
    private String codigoInterno;

    @Min(value = 15, message = "La duración mínima debe ser de 15 minutos")
    @Column(nullable = false, name = "duracion_promedio")
    private Integer duracionPromedioMinutos;

    @Min(value = 0, message = "El nivel de complejidad debe ser positivo")
    @Column(name = "nivel_complejidad")
    private Integer nivelComplejidad;

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

    @ManyToMany(mappedBy = "especialidades", fetch = FetchType.LAZY)
    private Set<User> doctores = new HashSet<>();

    @OneToMany(mappedBy = "especialidadRequerida", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TipoCita> tiposCita = new HashSet<>();
}