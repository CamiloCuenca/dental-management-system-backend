package edu.uniquindio.dentalmanagementsystembackend.controller;


import edu.uniquindio.dentalmanagementsystembackend.dto.JWT.TokenDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.account.ActualizarPerfilDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.account.CrearCuentaDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.account.LoginDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.account.PerfilDTO;
import edu.uniquindio.dentalmanagementsystembackend.exception.*;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.ServiciosCuenta;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cuenta")
@RequiredArgsConstructor
public class CuentaController {

    private final ServiciosCuenta serviciosCuenta;

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody LoginDTO loginDTO) {
        try {
            return ResponseEntity.ok(serviciosCuenta.login(loginDTO));
        } catch (Exception | UserNotFoundException | AccountInactiveException | InvalidPasswordException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/crear")
    public ResponseEntity<String> crearCuenta(@RequestBody CrearCuentaDTO crearCuentaDTO) {
        try {
            return ResponseEntity.ok(serviciosCuenta.crearCuenta(crearCuentaDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (EmailAlreadyExistsException e) {
            throw new RuntimeException(e);
        } catch (UserAlreadyExistsException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/perfil/{idNumber}")
    public ResponseEntity<PerfilDTO> obtenerPerfil(@PathVariable String idNumber) {
        try {
            return ResponseEntity.ok(serviciosCuenta.obtenerPerfil(idNumber));
        } catch (Exception | UserNotFoundException | InvalidIdFormatException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/perfil/{idNumber}")
    public ResponseEntity<String> actualizarPerfil(@PathVariable String idNumber, @RequestBody ActualizarPerfilDTO actualizarPerfilDTO) {
        try {
            serviciosCuenta.actualizarPerfil(idNumber, actualizarPerfilDTO);
            return ResponseEntity.ok("Perfil actualizado correctamente.");
        } catch (Exception | UserNotFoundException | InvalidIdFormatException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/eliminar/{idNumber}")
    public ResponseEntity<String> eliminarCuenta(@PathVariable String idNumber) {
        try {
            serviciosCuenta.eliminarCuenta(idNumber);
            return ResponseEntity.ok("Cuenta eliminada correctamente.");
        } catch (Exception | UserNotFoundException | InvalidIdFormatException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
