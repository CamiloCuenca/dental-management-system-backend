package edu.uniquindio.dentalmanagementsystembackend.controller;

import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoInventario;
import edu.uniquindio.dentalmanagementsystembackend.Enum.TipoProducto;
import edu.uniquindio.dentalmanagementsystembackend.dto.Inventario.InventarioDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.Inventario.InventarioDetalleDTO;
import edu.uniquindio.dentalmanagementsystembackend.entity.Inventario;
import edu.uniquindio.dentalmanagementsystembackend.repository.InventarioRepository;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.InventarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventario")
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioService inventarioService;
    private final InventarioRepository inventarioRepository;

    @PostMapping
    public ResponseEntity<?> registrarInventario(@RequestBody InventarioDTO inventarioDTO) {
        try {
            Inventario inventario = inventarioService.registrarInventario(inventarioDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(inventario);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarInventario(
            @PathVariable Long id,
            @RequestBody @Valid InventarioDTO inventarioDTO) {

        try {
            Inventario inventarioActualizado = inventarioService.actualizarInventario(id, inventarioDTO);
            return ResponseEntity.ok(inventarioActualizado);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarInventario(@PathVariable Long id) {
        try {
            inventarioService.eliminarInventario(id);
            return ResponseEntity.ok("Producto eliminado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<Page<InventarioDetalleDTO>> listarInventarioPaginado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<InventarioDetalleDTO> resultado = inventarioService.listarInventarioPaginado(page, size);
        return ResponseEntity.ok(resultado);
    }

    // Verción sin paginación
    @GetMapping("/todos")
    public ResponseEntity<List<InventarioDetalleDTO>> listarTodoElInventario() {
        return ResponseEntity.ok(inventarioService.listarTodoElInventario());
    }

    @PatchMapping("/{id}/cantidad")
    public ResponseEntity<?> actualizarCantidad(
            @PathVariable Long id,
            @RequestParam Integer cantidad) {

        try {
            Inventario inventario = inventarioService.actualizarCantidadDisponible(id, cantidad);
            return ResponseEntity.ok(inventario);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/abastecer")
    public ResponseEntity<?> abastecerProducto(
            @PathVariable Long id,
            @RequestParam Integer cantidad) {

        try {
            inventarioService.realizarAbastecimiento(id, cantidad);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Inventario>> buscarPorNombre(
            @RequestParam String nombre) {
        return ResponseEntity.ok(inventarioService.buscarPorNombre(nombre));
    }

    @GetMapping("/tipo")
    public ResponseEntity<List<Inventario>> buscarPorTipo(
            @RequestParam TipoProducto tipo) {
        return ResponseEntity.ok(inventarioService.buscarPorTipoProducto(tipo));
    }

    @GetMapping("/estado")
    public ResponseEntity<List<Inventario>> buscarPorEstado(
            @RequestParam EstadoInventario estado) {
        return ResponseEntity.ok(inventarioService.buscarPorEstado(estado));
    }

    @GetMapping("/bajo-minimo")
    public ResponseEntity<List<Inventario>> obtenerProductosBajoMinimo() {
        return ResponseEntity.ok(inventarioService.obtenerProductosPorDebajoMinimo());
    }



    @PutMapping("/{id}/usar")
    public ResponseEntity<?> registrarUsoProducto(
            @PathVariable Long id,
            @RequestParam Integer cantidad) {
        try {
            inventarioService.registrarUsoProducto(id, cantidad);
            return ResponseEntity.ok("Uso del producto registrado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
