package edu.uniquindio.dentalmanagementsystembackend.controller;

import edu.uniquindio.dentalmanagementsystembackend.dto.JWT.TokenDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.account.*;
import edu.uniquindio.dentalmanagementsystembackend.exception.*;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.ServiciosCuenta;
import edu.uniquindio.dentalmanagementsystembackend.service.impl.CaptchaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import javax.security.auth.login.AccountNotFoundException;

@RestController
@RequestMapping("/api/cuenta")
@RequiredArgsConstructor
@Slf4j
public class CuentaController {

    // Service for account-related operations
    private final ServiciosCuenta accountService;
    private final CaptchaService captchaService;

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
     * Endpoint for creating a new account with reCAPTCHA v3 verification.
     * @param registroDTO DTO con los datos de la cuenta y el token de reCAPTCHA
     * @return ResponseEntity with a success message if account creation is successful, or CONFLICT status if it fails.
     */
    @PostMapping("/register")
    public ResponseEntity<String> crearCuenta(@Valid @RequestBody RegistroConCaptchaDTO registroDTO) {
        try {
            // Verificar reCAPTCHA
            Boolean captchaValid = captchaService.verifyCaptcha(registroDTO.captchaToken()).block();
            if (!captchaValid) {
                log.warn("reCAPTCHA verification failed for account creation");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Verificación de seguridad fallida. Por favor, inténtalo de nuevo.");
            }

            // Crear la cuenta usando el DTO convertido
            CrearCuentaDTO cuentaDTO = registroDTO.toCrearCuentaDTO();
            return ResponseEntity.status(HttpStatus.CREATED).body(accountService.crearCuenta(cuentaDTO));
        } catch (EmailAlreadyExistsException | UserAlreadyExistsException | Exception | DatabaseOperationException |
                 EmailSendingException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    /**
     * Endpoint for creating a new account without reCAPTCHA (for backward compatibility).
     * @param cuentaDTO Data transfer object containing account details.
     * @return ResponseEntity with a success message if account creation is successful, or CONFLICT status if it fails.
     */
    @PostMapping("/register-no-captcha")
    public ResponseEntity<String> crearCuentaSinCaptcha(@RequestBody CrearCuentaDTO cuentaDTO) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(accountService.crearCuenta(cuentaDTO));
        } catch (EmailAlreadyExistsException | UserAlreadyExistsException | Exception | DatabaseOperationException |
                 EmailSendingException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    /**
     * Endpoint for deleting an account.
     * @param accountId ID of the account to delete.
     * @return ResponseEntity with no content if deletion is successful, or appropriate error status if it fails.
     * @throws InvalidIdFormatException 
     * @throws UserNotFoundException 
     */
    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> eliminarCuenta(@PathVariable Long accountId) throws UserNotFoundException, InvalidIdFormatException {
        try {
            accountService.eliminarCuenta(accountId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint for activating an account.
     * @param activateAccountDTO Data transfer object containing activation information.
     * @return ResponseEntity with a success message if activation is successful, or appropriate error status if it fails.
     */
    @PostMapping("/activate")
    public ResponseEntity<String> activarCuenta(@RequestBody ActivateAccountDTO activateAccountDTO) {
        try {
            return ResponseEntity.ok(accountService.activateAccount(activateAccountDTO));
        } catch (AccountAlreadyActiveException | ValidationCodeExpiredException | AccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");
        }
    }

    /**
     * Endpoint for sending activation code.
     * @param emailDTO Data transfer object containing email information.
     * @return ResponseEntity with a success message if code sending is successful, or appropriate error status if it fails.
     */
    @PostMapping("/send-active-code")
    public ResponseEntity<String> enviarCodigoActivacion(@RequestBody EmailDTO emailDTO) {
        try {
            return ResponseEntity.ok(accountService.sendActiveCode(emailDTO.email()));
        } catch (EmailNotFoundException | AccountAlreadyActiveException | Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Endpoint for sending password recovery code.
     * @param emailDTO Data transfer object containing email information.
     * @return ResponseEntity with a success message if code sending is successful, or appropriate error status if it fails.
     */
    @PostMapping("/send-recovery-code")
    public ResponseEntity<String> enviarCodigoRecuperacion(@RequestBody EmailDTO emailDTO) {
        try {
            return ResponseEntity.ok(accountService.sendPasswordRecoveryCode(emailDTO.email()));
        } catch (EmailNotFoundException | Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Endpoint for changing password using recovery code.
     * @param changePasswordDTO Data transfer object containing password change information.
     * @return ResponseEntity with a success message if password change is successful, or appropriate error status if it fails.
     * @throws Exception 
     */
    @PostMapping("/change-password-code")
    public ResponseEntity<String> cambiarContraseñaConCodigo(@RequestBody ChangePasswordCodeDTO changePasswordDTO) throws Exception {
        try {
            return ResponseEntity.ok(accountService.changePasswordCode(changePasswordDTO));
        } catch (InvalidValidationCodeException | ValidationCodeExpiredException | PasswordsDoNotMatchException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Endpoint for updating password.
     * @param accountId ID of the account.
     * @param updatePasswordDTO Data transfer object containing password update information.
     * @return ResponseEntity with a success message if password update is successful, or appropriate error status if it fails.
     * @throws Exception 
     */
    @PutMapping("/{accountId}/update-password")
    public ResponseEntity<String> actualizarContraseña(@PathVariable Long accountId, @RequestBody UpdatePasswordDTO updatePasswordDTO) throws Exception {
        try {
            return ResponseEntity.ok(accountService.updatePassword(accountId, updatePasswordDTO));
        } catch (AccountNotFoundException | InvalidCurrentPasswordException | PasswordMismatchException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Endpoint for getting user profile.
     * @param accountId ID of the account.
     * @return ResponseEntity with user profile information if successful, or appropriate error status if it fails.
     */
    @GetMapping("/{accountId}/profile")
    public ResponseEntity<PerfilDTO> obtenerPerfil(@PathVariable Long accountId) {
        try {
            return ResponseEntity.ok(accountService.obtenerPerfil(accountId));
        } catch (UserNotFoundException | AccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Endpoint for updating user profile.
     * @param accountId ID of the account.
     * @param actualizarUsuarioDTO Data transfer object containing updated user information.
     * @return ResponseEntity with a success message if update is successful, or appropriate error status if it fails.
     * @throws Exception 
     */
    @PutMapping("/{accountId}/profile")
    public ResponseEntity<String> actualizarPerfil(@PathVariable Long accountId, @RequestBody ActualizarUsuarioDTO actualizarUsuarioDTO) throws Exception {
        try {
            return ResponseEntity.ok(accountService.actualizarUsuario(accountId, actualizarUsuarioDTO));
        } catch (UserNotFoundException | AccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
