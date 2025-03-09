package edu.uniquindio.dentalmanagementsystembackend.controller;


import edu.uniquindio.dentalmanagementsystembackend.dto.JWT.TokenDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.account.*;
import edu.uniquindio.dentalmanagementsystembackend.exception.*;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.ServiciosCuenta;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;

@RestController
@RequestMapping("/api/cuenta")
@RequiredArgsConstructor
public class CuentaController {

    private final ServiciosCuenta accountService;


    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody LoginDTO loginDTO) {
        try {
            TokenDTO token = accountService.login(loginDTO);
            return ResponseEntity.ok(token);
        } catch (UserNotFoundException | AccountInactiveException | InvalidPasswordException | Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> crearCuenta(@RequestBody CrearCuentaDTO cuentaDTO) {
        try {
            String accountId = accountService.crearCuenta(cuentaDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(accountId);
        } catch (EmailAlreadyExistsException | UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");
        }
    }

    @GetMapping("/perfil/{accountId}")
    public ResponseEntity<PerfilDTO> obtenerPerfil(@PathVariable Long accountId) {
        try {
            PerfilDTO perfil = accountService.obtenerPerfil(accountId);
            return ResponseEntity.ok(perfil);
        } catch (UserNotFoundException | Exception | InvalidIdFormatException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/perfil/{accountId}")
    public ResponseEntity<Void> actualizarPerfil(@PathVariable Long accountId, @RequestBody ActualizarPerfilDTO actualizarPerfilDTO) {
        try {
            accountService.actualizarPerfil(accountId, actualizarPerfilDTO);
            return ResponseEntity.noContent().build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (InvalidIdFormatException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> eliminarCuenta(@PathVariable Long accountId) {
        try {
            accountService.eliminarCuenta(accountId);
            return ResponseEntity.noContent().build();
        } catch (Exception | UserNotFoundException | InvalidIdFormatException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/activate")
    public ResponseEntity<String> activateAccount(@RequestBody ActivateAccountDTO activateAccountDTO) {
        try {
            String result = accountService.activateAccount(activateAccountDTO);
            return ResponseEntity.ok(result);
        } catch (AccountAlreadyActiveException | ValidationCodeExpiredException | Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/send-activation-code")
    public ResponseEntity<String> sendActiveCode(@RequestParam String email) {
        try {
            String response = accountService.sendActiveCode(email);
            return ResponseEntity.ok(response);
        } catch (EmailNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordCodeDTO changePasswordDTO) {
        try {
            String response = accountService.changePasswordCode(changePasswordDTO);
            return ResponseEntity.ok(response);
        } catch (InvalidValidationCodeException | ValidationCodeExpiredException | PasswordsDoNotMatchException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");
        }
    }

    @PutMapping("/update-password/{id}")
    public ResponseEntity<String> updatePassword(@PathVariable Long id, @RequestBody UpdatePasswordDTO updatePasswordDTO)
            throws Exception, InvalidCurrentPasswordException, PasswordMismatchException {
        String response = accountService.updatePassword(id, updatePasswordDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/send-recovery-code")
    public ResponseEntity<String> sendPasswordRecoveryCode(@RequestParam String email)
            throws Exception, EmailNotFoundException {
        String response = accountService.sendPasswordRecoveryCode(email);
        return ResponseEntity.ok(response);
    }

}
