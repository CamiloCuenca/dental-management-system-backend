package edu.uniquindio.dentalmanagementsystembackend.entity;

import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoInventario;
import edu.uniquindio.dentalmanagementsystembackend.Enum.TipoProducto;
import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Entity
@Table(name = "inventario")
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_producto", nullable = false, length = 50)
    private TipoProducto tipoProducto;

    @Column(name = "cantidad_disponible", nullable = false)
    private Integer cantidadDisponible;

    @Column(name = "cantidad_minima", nullable = false)
    private Integer cantidadMinima; // Nivel mínimo antes de generar alerta

    @Column(name = "precio_unitario", nullable = false)
    private Double precioUnitario;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento; // Para materiales perecederos

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoInventario estado;

    @Column(name = "ubicacion", length = 100)
    private String ubicacion; // Ubicación física en la clínica

    @Column(name = "fecha_ultimo_abastecimiento")
    private LocalDate fechaUltimoAbastecimiento;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDate fechaRegistro;

    @Column(name = "es_sterilizable")
    private Boolean esSterilizable; // Para instrumentos reutilizables

    @Column(name = "vida_util_sterilizacion")
    private Integer vidaUtilSterilizacion; // En número de usos

}