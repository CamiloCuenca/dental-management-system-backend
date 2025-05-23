package edu.uniquindio.dentalmanagementsystembackend.service.Interfaces;

import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoInventario;
import edu.uniquindio.dentalmanagementsystembackend.Enum.TipoProducto;
import edu.uniquindio.dentalmanagementsystembackend.dto.Inventario.InventarioDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.Inventario.InventarioDetalleDTO;
import edu.uniquindio.dentalmanagementsystembackend.entity.Inventario;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface InventarioService {

    // Operaciones CRUD básicas
    Inventario registrarInventario(InventarioDTO inventarioDTO) throws Exception;
    Inventario actualizarInventario(Long id, InventarioDTO inventarioDTO) throws Exception;
    void eliminarInventario(Long id) throws Exception;
    InventarioDetalleDTO obtenerInventarioPorId(Long id) throws Exception;
    Page<InventarioDetalleDTO> listarInventarioPaginado(int page, int size);
    public List<InventarioDetalleDTO> listarTodoElInventario();

    // Operaciones específicas de gestión
    Inventario actualizarCantidadDisponible(Long id, Integer cantidad) throws Exception;
    void realizarAbastecimiento(Long id, Integer cantidadAgregada) throws Exception;
    List<Inventario> buscarPorNombre(String nombre);
    List<Inventario> buscarPorTipoProducto(TipoProducto tipoProducto);
    List<Inventario> buscarPorEstado(EstadoInventario estado);

    // Operaciones de alertas y reportes
    List<Inventario> obtenerProductosPorDebajoMinimo();
    List<Inventario> obtenerProductosProximosAVencer(LocalDate fechaLimite);
    List<Inventario> obtenerProductosNecesitanSterilizacion();


    void registrarUsoProducto(Long idProducto, Integer cantidadUsada) throws Exception;
}
