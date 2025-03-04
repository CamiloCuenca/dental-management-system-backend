package edu.uniquindio.dentalmanagementsystembackend.controller;

import edu.uniquindio.dentalmanagementsystembackend.dto.ActualizarPerfilDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.CrearCuentaDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.PerfilDTO;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.ServiciosCuenta;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cuenta")
@RequiredArgsConstructor
public class CuentaController {

    private final ServiciosCuenta serviciosCuenta;

    @PostMapping("/crear")
    public ResponseEntity<String> crearCuenta(@RequestBody CrearCuentaDTO cuentaDTO) {
        try {
            String idCuenta = serviciosCuenta.crearCuenta(cuentaDTO);
            return ResponseEntity.ok(idCuenta);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{idNumber}")
    public ResponseEntity<PerfilDTO> obtenerPerfil(@PathVariable String idNumber) {
        try {
            return ResponseEntity.ok(serviciosCuenta.obtenerPerfil(idNumber));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{idNumber}")
    public ResponseEntity<Void> actualizarPerfil(@PathVariable String idNumber,
                                                 @RequestBody ActualizarPerfilDTO actualizarPerfilDTO) {
        try {
            serviciosCuenta.actualizarPerfil(idNumber, actualizarPerfilDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{idNumber}")
    public ResponseEntity<Void> eliminarCuenta(@PathVariable String idNumber) {
        try {
            serviciosCuenta.eliminarCuenta(idNumber);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
