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

    // Service for account-related operations
    private final ServiciosCuenta accountService;

    /**
     * Endpoint for user login.
     * @param loginDTO Data transfer object containing login credentials.
     * @return ResponseEntity with a TokenDTO if login is successful, or UNAUTHORIZED status if it fails.
     */
    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody LoginDTO loginDTO) {
        try {
            return ResponseEntity.ok(accountService.login(loginDTO));
        } catch (UserNotFoundException | AccountInactiveException | InvalidPasswordException | Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    /**
     * Endpoint for creating a new account.
     * @param cuentaDTO Data transfer object containing account details.
     * @return ResponseEntity with a success message if account creation is successful, or CONFLICT status if it fails.
     */
    @PostMapping("/register")
    public ResponseEntity<String> crearCuenta(@RequestBody CrearCuentaDTO cuentaDTO) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(accountService.crearCuenta(cuentaDTO));
        } catch (EmailAlreadyExistsException | UserAlreadyExistsException | Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    /**
     * Endpoint for retrieving a user profile.
     * @param accountId ID of the account to retrieve.
     * @return ResponseEntity with the user profile if found, or appropriate error status if it fails.
     */
    @GetMapping("/perfil/{accountId}")
    public ResponseEntity<PerfilDTO> obtenerPerfil(@PathVariable Long accountId) {
        try {
            return ResponseEntity.ok(accountService.obtenerPerfil(accountId));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (InvalidIdFormatException | Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * Endpoint for updating a user profile.
     * @param accountId ID of the account to update.
     * @param actualizarPerfilDTO Data transfer object containing updated profile details.
     * @return ResponseEntity with no content if update is successful, or appropriate error status if it fails.
     */
    @PutMapping("/perfil/{accountId}")
    public ResponseEntity<Void> actualizarPerfil(@PathVariable Long accountId, @RequestBody ActualizarPerfilDTO actualizarPerfilDTO) {
        try {
            accountService.actualizarPerfil(accountId, actualizarPerfilDTO);
            return ResponseEntity.noContent().build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (InvalidIdFormatException | Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Endpoint for deleting an account.
     * @param accountId ID of the account to delete.
     * @return ResponseEntity with no content if deletion is successful, or appropriate error status if it fails.
     */
    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> eliminarCuenta(@PathVariable Long accountId) {
        try {
            accountService.eliminarCuenta(accountId);
            return ResponseEntity.noContent().build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (InvalidIdFormatException | Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Endpoint for activating an account.
     * @param activateAccountDTO Data transfer object containing activation details.
     * @return ResponseEntity with a success message if activation is successful, or appropriate error status if it fails.
     */
    @PostMapping("/activate")
    public ResponseEntity<String> activateAccount(@RequestBody ActivateAccountDTO activateAccountDTO) {
        try {
            return ResponseEntity.ok(accountService.activateAccount(activateAccountDTO));
        } catch (AccountAlreadyActiveException | ValidationCodeExpiredException | Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Endpoint for sending an activation code.
     * @param email Email address to send the activation code to.
     * @return ResponseEntity with a success message if sending is successful, or appropriate error status if it fails.
     */
    @PostMapping("/send-activation-code")
    public ResponseEntity<String> sendActiveCode(@RequestParam String email) {
        try {
            return ResponseEntity.ok(accountService.sendActiveCode(email));
        } catch (EmailNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Endpoint for changing the password using a code.
     * @param changePasswordDTO Data transfer object containing password change details.
     * @return ResponseEntity with a success message if password change is successful, or appropriate error status if it fails.
     */
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordCodeDTO changePasswordDTO) {
        try {
            return ResponseEntity.ok(accountService.changePasswordCode(changePasswordDTO));
        } catch (InvalidValidationCodeException | ValidationCodeExpiredException | PasswordsDoNotMatchException | Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Endpoint for updating the password.
     * @param id ID of the account to update the password for.
     * @param updatePasswordDTO Data transfer object containing new password details.
     * @return ResponseEntity with no content if update is successful, or appropriate error status if it fails.
     */
    @PutMapping("/update-password/{id}")
    public ResponseEntity<Void> updatePassword(@PathVariable Long id, @RequestBody UpdatePasswordDTO updatePasswordDTO) {
        try {
            accountService.updatePassword(id, updatePasswordDTO);
            return ResponseEntity.noContent().build();
        } catch (InvalidCurrentPasswordException | PasswordMismatchException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Endpoint for sending a password recovery code.
     * @param email Email address to send the recovery code to.
     * @return ResponseEntity with a success message if sending is successful, or appropriate error status if it fails.
     */
    @PostMapping("/send-recovery-code")
    public ResponseEntity<String> sendPasswordRecoveryCode(@RequestParam String email) {
        try {
            return ResponseEntity.ok(accountService.sendPasswordRecoveryCode(email));
        } catch (EmailNotFoundException | Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
