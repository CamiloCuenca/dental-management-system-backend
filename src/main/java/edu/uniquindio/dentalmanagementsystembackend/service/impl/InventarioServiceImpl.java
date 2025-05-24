package edu.uniquindio.dentalmanagementsystembackend.service.impl;

import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoInventario;
import edu.uniquindio.dentalmanagementsystembackend.Enum.TipoProducto;
import edu.uniquindio.dentalmanagementsystembackend.dto.Inventario.InventarioDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.Inventario.InventarioDetalleDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.account.EmailDTO;
import edu.uniquindio.dentalmanagementsystembackend.entity.Inventario;
import edu.uniquindio.dentalmanagementsystembackend.repository.CuentaRepository;
import edu.uniquindio.dentalmanagementsystembackend.repository.InventarioRepository;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.EmailService;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.InventarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InventarioServiceImpl implements InventarioService {

    private final InventarioRepository inventarioRepository;
    private final EmailService email;
    private final CuentaRepository cuentaRepository;


    @Override
    @Transactional
    public Inventario registrarInventario(InventarioDTO inventarioDTO) throws Exception {
        // 1. Validaciones previas
        validarDatosInventario(inventarioDTO);

        // 2. Verificar que no exista un producto con el mismo nombre
        if (inventarioRepository.existsByNombre(inventarioDTO.nombre())) {
            throw new Exception("Ya existe un producto en el inventario con el nombre: " + inventarioDTO.nombre());
        }

        // 3. Mapear DTO a entidad
        Inventario nuevoInventario = new Inventario();
        nuevoInventario.setNombre(inventarioDTO.nombre());
        nuevoInventario.setDescripcion(inventarioDTO.descripcion());
        nuevoInventario.setTipoProducto(inventarioDTO.tipoProducto());
        nuevoInventario.setCantidadDisponible(inventarioDTO.cantidadDisponible());
        nuevoInventario.setCantidadMinima(inventarioDTO.cantidadMinima());
        nuevoInventario.setPrecioUnitario(inventarioDTO.precioUnitario());
        nuevoInventario.setFechaVencimiento(inventarioDTO.fechaVencimiento());
        nuevoInventario.setEsSterilizable(inventarioDTO.esSterilizable());
        nuevoInventario.setVidaUtilSterilizacion(inventarioDTO.vidaUtilSterilizacion());

        // 4. Establecer valores por defecto
        nuevoInventario.setFechaRegistro(LocalDate.now());
        nuevoInventario.setEstado(calcularEstadoInicial(nuevoInventario));

        // 5. Guardar en la base de datos
        return inventarioRepository.save(nuevoInventario);
    }

    private void validarDatosInventario(InventarioDTO inventarioDTO) throws Exception {
        // Validar campos obligatorios
        if (inventarioDTO.nombre() == null || inventarioDTO.nombre().isBlank()) {
            throw new Exception("El nombre del producto es obligatorio");
        }

        if (inventarioDTO.tipoProducto() == null) {
            throw new Exception("El tipo de producto es obligatorio");
        }

        // Validar cantidades
        if (inventarioDTO.cantidadDisponible() == null || inventarioDTO.cantidadDisponible() < 0) {
            throw new Exception("La cantidad disponible debe ser un número positivo");
        }

        if (inventarioDTO.cantidadMinima() == null || inventarioDTO.cantidadMinima() < 0) {
            throw new Exception("La cantidad mínima debe ser un número positivo");
        }

        // Validar precio
        if (inventarioDTO.precioUnitario() == null || inventarioDTO.precioUnitario() <= 0) {
            throw new Exception("El precio unitario debe ser mayor a cero");
        }

        // Validar fechas
        if (inventarioDTO.fechaVencimiento() != null && inventarioDTO.fechaVencimiento().isBefore(LocalDate.now())) {
            throw new Exception("La fecha de vencimiento no puede ser en el pasado");
        }

        // Validar consistencia para productos esterilizables
        if (inventarioDTO.esSterilizable() != null && inventarioDTO.esSterilizable()) {
            if (inventarioDTO.vidaUtilSterilizacion() == null || inventarioDTO.vidaUtilSterilizacion() <= 0) {
                throw new Exception("Debe especificar la vida útil de esterilización para productos reutilizables");
            }
        }
    }

    private EstadoInventario calcularEstadoInicial(Inventario inventario) {
        if (inventario.getCantidadDisponible() <= 0) {
            return EstadoInventario.AGOTADO;
        } else if (inventario.getCantidadDisponible() < inventario.getCantidadMinima()) {
            return EstadoInventario.EN_REPOSICION;
        } else if (inventario.getFechaVencimiento() != null &&
                inventario.getFechaVencimiento().isBefore(LocalDate.now())) {
            return EstadoInventario.VENCIDO;
        } else {
            return EstadoInventario.DISPONIBLE;
        }
    }

    @Override
    @Transactional
    public Inventario actualizarInventario(Long id, InventarioDTO inventarioDTO) throws Exception {
        // 1. Validar datos de entrada
        validarDatosInventario(inventarioDTO);

        // 2. Buscar el inventario existente
        Inventario inventarioExistente = inventarioRepository.findById(id)
                .orElseThrow(() -> new Exception("No se encontró el producto con ID: " + id));

        // 3. Validar cambio de nombre único (si se modificó)
        if (!inventarioExistente.getNombre().equals(inventarioDTO.nombre()) &&
                inventarioRepository.existsByNombre(inventarioDTO.nombre())) {
            throw new Exception("Ya existe otro producto con el nombre: " + inventarioDTO.nombre());
        }

        // 4. Actualizar campos modificables
        inventarioExistente.setNombre(inventarioDTO.nombre());
        inventarioExistente.setDescripcion(inventarioDTO.descripcion());
        inventarioExistente.setTipoProducto(inventarioDTO.tipoProducto());
        inventarioExistente.setCantidadMinima(inventarioDTO.cantidadMinima());
        inventarioExistente.setPrecioUnitario(inventarioDTO.precioUnitario());
        inventarioExistente.setFechaVencimiento(inventarioDTO.fechaVencimiento());

        // 5. Lógica especial para productos esterilizables
        if (inventarioDTO.esSterilizable() != null) {
            inventarioExistente.setEsSterilizable(inventarioDTO.esSterilizable());
            if (inventarioDTO.esSterilizable()) {
                if (inventarioDTO.vidaUtilSterilizacion() == null || inventarioDTO.vidaUtilSterilizacion() <= 0) {
                    throw new Exception("Debe especificar una vida útil válida para productos esterilizables");
                }
                inventarioExistente.setVidaUtilSterilizacion(inventarioDTO.vidaUtilSterilizacion());
            } else {
                inventarioExistente.setVidaUtilSterilizacion(null);
            }
        }

        // 6. Recalcular estado basado en nuevos valores
        actualizarEstadoInventario(inventarioExistente);

        // 7. Guardar cambios
        return inventarioRepository.save(inventarioExistente);
    }

    // Método auxiliar para actualizar estado
    private void actualizarEstadoInventario(Inventario inventario) {
        if (inventario.getCantidadDisponible() <= 0) {
            inventario.setEstado(EstadoInventario.AGOTADO);
        } else if (inventario.getCantidadDisponible() < inventario.getCantidadMinima()) {
            inventario.setEstado(EstadoInventario.EN_REPOSICION);
        } else if (inventario.getFechaVencimiento() != null &&
                inventario.getFechaVencimiento().isBefore(LocalDate.now())) {
            inventario.setEstado(EstadoInventario.VENCIDO);
        } else {
            inventario.setEstado(EstadoInventario.DISPONIBLE);
        }
    }

    @Override
    @Transactional
    public void eliminarInventario(Long id) throws Exception {
        //  Verificar si el item existe
        Inventario inventario = inventarioRepository.findById(id)
                .orElseThrow(() -> new Exception("No se encontró el producto con ID: " + id));

        // Realizar eliminación lógica
        inventario.setEstado(EstadoInventario.ELIMINADO);
        inventarioRepository.save(inventario);

        // O para borrado físico directo:
        // inventarioRepository.delete(inventario);
    }

    @Override
    @Transactional(readOnly = true)
    public InventarioDetalleDTO obtenerInventarioPorId(Long id) throws Exception {
        return inventarioRepository.findProjectedById(id)
                .orElseThrow(() -> new Exception("No se encontró el producto con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InventarioDetalleDTO> listarInventarioPaginado(int page, int size) {
        Page<Inventario> inventariosPage = inventarioRepository.findAll(PageRequest.of(page, size));
        return inventariosPage.map(this::convertirADTO);
    }


    @Override
    @Transactional(readOnly = true)
    public List<InventarioDetalleDTO> listarTodoElInventario() {
        return inventarioRepository.findAll().stream()
                .map(this::convertirADTO)
                .toList();
    }

    private InventarioDetalleDTO convertirADTO(Inventario inventario) {
        return new InventarioDetalleDTO(
                inventario.getId(),
                inventario.getNombre(),
                inventario.getDescripcion(),
                inventario.getTipoProducto(),
                inventario.getCantidadDisponible(),
                inventario.getCantidadMinima(),
                inventario.getEstado()
        );
    }

    @Override
    @Transactional
    public Inventario actualizarCantidadDisponible(Long id, Integer cantidad) throws Exception {
        // 1. Validar que la cantidad no sea negativa
        if (cantidad < 0) {
            throw new Exception("La cantidad no puede ser negativa");
        }

        // 2. Obtener el inventario existente
        Inventario inventario = inventarioRepository.findById(id)
                .orElseThrow(() -> new Exception("No se encontró el producto con ID: " + id));

        // 3. Actualizar la cantidad
        inventario.setCantidadDisponible(cantidad);

        // 4. Actualizar el estado según la nueva cantidad
        actualizarEstadoInventario(inventario);

        // 5. Guardar y retornar
        return inventarioRepository.save(inventario);
    }



    @Override
    @Transactional
    public void realizarAbastecimiento(Long id, Integer cantidadAgregada) throws Exception {
        // 1. Validar que la cantidad sea positiva
        if (cantidadAgregada <= 0) {
            throw new Exception("La cantidad a agregar debe ser mayor que cero");
        }

        // 2. Obtener el item del inventario
        Inventario inventario = inventarioRepository.findById(id)
                .orElseThrow(() -> new Exception("No se encontró el producto con ID: " + id));

        // 3. Actualizar la cantidad disponible
        int nuevaCantidad = inventario.getCantidadDisponible() + cantidadAgregada;
        inventario.setCantidadDisponible(nuevaCantidad);

        // 4. Actualizar la fecha de último abastecimiento
        inventario.setFechaUltimoAbastecimiento(LocalDate.now());

        // 5. Actualizar el estado automáticamente
        actualizarEstadoInventario(inventario);

        // 6. Guardar los cambios
        inventarioRepository.save(inventario);
    }

  @Override
  @Transactional
  public void registrarUsoProducto(Long idProducto, Integer cantidadUsada) throws Exception {
      // 1. Validar que la cantidad usada sea positiva
      if (cantidadUsada <= 0) {
          throw new Exception("La cantidad usada debe ser mayor que cero");
      }

      // 2. Obtener el producto del inventario
      Inventario inventario = inventarioRepository.findById(idProducto)
              .orElseThrow(() -> new Exception("No se encontró el producto con ID: " + idProducto));

      // 3. Validar que haya suficiente stock
      if (inventario.getCantidadDisponible() < cantidadUsada) {
          throw new Exception("No hay suficiente stock disponible para el producto: " + inventario.getNombre());
      }

      // 4. Descontar la cantidad usada del stock
      inventario.setCantidadDisponible(inventario.getCantidadDisponible() - cantidadUsada);

      // 5. Verificar si la cantidad disponible está por debajo del mínimo
      if (inventario.getCantidadDisponible() < inventario.getCantidadMinima()) {
          System.out.println("El producto " + inventario.getNombre() + " está por debajo de la cantidad mínima.");

          // Obtener correos de los administradores
          List<String> correosAdministradores = cuentaRepository.obtenerCorreosAdministradores();

          // Notificar a los administradores
          notificarAdministradoresCantidadMinima(inventario, correosAdministradores);
      }

      // 6. Actualizar el estado del inventario
      actualizarEstadoInventario(inventario);

      // 7. Guardar los cambios
      inventarioRepository.save(inventario);
  }
    private void notificarAdministradoresCantidadMinima(Inventario inventario, List<String> correosAdministradores) {
        // Construcción del mensaje HTML
        String htmlMessage = """
        <html>
        <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
            <div style="max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 5px;">
                <h2 style="color: #2c3e50;">Alerta: Producto con cantidad mínima</h2>
                <p>Estimado administrador,</p>
                <p>El producto <strong>%s</strong> ha alcanzado o está por debajo de su cantidad mínima.</p>
                <ul>
                    <li><strong>ID del producto:</strong> %d</li>
                    <li><strong>Cantidad disponible:</strong> %d</li>
                    <li><strong>Cantidad mínima:</strong> %d</li>
                </ul>
                <p>Por favor, tome las medidas necesarias para reabastecer este producto.</p>
                <p>Atentamente,<br/>El sistema de gestión de inventario</p>
                <hr style="border: 1px solid #eee; margin: 20px 0;">
                <p style="font-size: 12px; color: #777;">Este es un correo automático, por favor no responda.</p>
            </div>
        </body>
        </html>
        """.formatted(
                inventario.getNombre(),
                inventario.getId(),
                inventario.getCantidadDisponible(),
                inventario.getCantidadMinima()
        );

        // Envío del correo a cada administrador
        for (String correo : correosAdministradores) {
            try {
                email.sendMail(new EmailDTO(correo, "Alerta: Producto con cantidad mínima", htmlMessage));
            } catch (Exception e) {
                e.printStackTrace(); // Manejo de excepciones, podrías usar un logger aquí
            }
        }
    }


    @Override
    @Transactional(readOnly = true)
    public List<Inventario> buscarPorNombre(String nombre) {
        return inventarioRepository.findByNombreContainingIgnoreCase(nombre);
    }


    @Override
    @Transactional(readOnly = true)
    public List<Inventario> buscarPorTipoProducto(TipoProducto tipoProducto) {
        return inventarioRepository.findByTipoProducto(tipoProducto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inventario> buscarPorEstado(EstadoInventario estado) {
        return inventarioRepository.findByEstado(estado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inventario> obtenerProductosPorDebajoMinimo() {
        return inventarioRepository.findByCantidadDisponibleLessThanCantidadMinima();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inventario> obtenerProductosProximosAVencer(LocalDate fechaLimite) {
        // Validar fecha límite
        if (fechaLimite == null) {
            fechaLimite = LocalDate.now().plusWeeks(2); // 2 semanas por defecto
        }

        return inventarioRepository.findByFechaVencimientoBetweenAndEstadoNot(
                LocalDate.now(),
                fechaLimite
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inventario> obtenerProductosNecesitanSterilizacion() {
        return inventarioRepository.findProductosParaEsterilizar(5);
    }
}
