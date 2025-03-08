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

}
