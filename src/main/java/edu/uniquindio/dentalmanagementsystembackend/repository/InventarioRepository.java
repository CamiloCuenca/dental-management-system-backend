package edu.uniquindio.dentalmanagementsystembackend.repository;


import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoInventario;
import edu.uniquindio.dentalmanagementsystembackend.Enum.TipoProducto;
import edu.uniquindio.dentalmanagementsystembackend.dto.Inventario.InventarioDetalleDTO;
import edu.uniquindio.dentalmanagementsystembackend.entity.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    boolean existsByNombre(String nombre);

    @Query("SELECT new edu.uniquindio.dentalmanagementsystembackend.dto.Inventario.InventarioDetalleDTO(" +
            "i.id, i.nombre, i.descripcion, i.tipoProducto, " +
            "i.cantidadDisponible, i.cantidadMinima, i.estado) " +
            "FROM Inventario i WHERE i.id = :id")
    Optional<InventarioDetalleDTO> findProjectedById(@Param("id") Long id);

    // Búsqueda por nombre (insensible a mayúsculas/minúsculas)
    List<Inventario> findByNombreContainingIgnoreCase(String nombre);

    // Búsqueda por tipo de producto
    List<Inventario> findByTipoProducto(TipoProducto tipoProducto);

    // Búsqueda por estado
    List<Inventario> findByEstado(EstadoInventario estado);

    // Productos por debajo del mínimo
    @Query("SELECT i FROM Inventario i WHERE i.cantidadDisponible < i.cantidadMinima")
    List<Inventario> findByCantidadDisponibleLessThanCantidadMinima();


    @Query("SELECT i FROM Inventario i " +
            "WHERE i.fechaVencimiento BETWEEN :fechaInicio AND :fechaFin " +
            "AND i.estado <> 'VENCIDO'")
    List<Inventario> findByFechaVencimientoBetweenAndEstadoNot(
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);


    @Query("SELECT i FROM Inventario i " +
            "WHERE i.esSterilizable = TRUE " +
            "AND (i.vidaUtilSterilizacion <= :umbral OR i.vidaUtilSterilizacion IS NULL) " +
            "AND i.estado <> 'DAÑADO' " +
            "ORDER BY i.vidaUtilSterilizacion ASC")
    List<Inventario> findProductosParaEsterilizar(@Param("umbral") Integer umbral);






}