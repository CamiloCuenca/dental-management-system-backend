package edu.uniquindio.dentalmanagementsystembackend.Account;

import edu.uniquindio.dentalmanagementsystembackend.dto.account.*;
import edu.uniquindio.dentalmanagementsystembackend.exception.*;
import edu.uniquindio.dentalmanagementsystembackend.repository.CuentaRepository;
import edu.uniquindio.dentalmanagementsystembackend.repository.UserRepository;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.ServiciosCuenta;

import javax.security.auth.login.AccountNotFoundException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for account-related functionalities. This class contains various test cases
 * for operations offered by the ServiciosCuenta service, such as login, account creation,
 * activation, deletion, password updates, and profile management.
 */
@SpringBootTest
public class AccountTest {

    @Autowired
    ServiciosCuenta serviciosCuenta;

    /**
     * Test for user login.
     *
     * @throws Exception                if a general error occurs.
     * @throws UserNotFoundException    if the user is not found.
     * @throws InvalidPasswordException if the password is invalid.
     * @throws AccountInactiveException if the account is inactive.
     */
    @Test
    void testLogin() throws Exception, UserNotFoundException, InvalidPasswordException, AccountInactiveException {
        LoginDTO loginDTO = new LoginDTO(
                "1001277430",
                "C@ntraseña123");

        serviciosCuenta.login(loginDTO);
    }

    /**
     * Test for creating an account.
     *
     * @throws Exception                   if a general error occurs.
     * @throws EmailAlreadyExistsException if the email already exists.
     * @throws UserAlreadyExistsException  if the user already exists.
     */
    @Test
    void testGuardarCuentas() throws Exception, EmailAlreadyExistsException, UserAlreadyExistsException,
            DatabaseOperationException, EmailSendingException {
        CrearCuentaDTO crearCuentaDTO = new CrearCuentaDTO(
                "1001277430", // idNumber
                "Brandon", // name
                "Acevedo castañeda", // lastName
                "3153033412", // phoneNumber
                "carrera-15#3", // address
                LocalDate.parse("2000-05-20"), // fechaNacimiento (LocalDate)
                "brandone.acevedoc@uqvirtual.edu.co", // email
                "M@mahermosa123" // password
        );
        serviciosCuenta.crearCuenta(crearCuentaDTO);
    }

    /**
     * Test for deleting an account.
     *
     * @throws Exception                if a general error occurs.
     * @throws UserNotFoundException    if the user is not found.
     * @throws InvalidIdFormatException if the ID format is invalid.
     */
    @Test
    void testEliminarCuentas() throws Exception, UserNotFoundException, InvalidIdFormatException {
        serviciosCuenta.eliminarCuenta(49L);
    }


    /**
     * Test for activating an account.
     *
     * @throws Exception                      if a general error occurs.
     * @throws AccountAlreadyActiveException  if the account is already active.
     * @throws ValidationCodeExpiredException if the validation code has expired.
     */
    @Test
    void testActivarCuenta() throws Exception, AccountAlreadyActiveException, ValidationCodeExpiredException {
        ActivateAccountDTO activateAccountDTO = new ActivateAccountDTO(
                "91236",
                "brandone.acevedoc@uqvirtual.edu.co");
        serviciosCuenta.activateAccount(activateAccountDTO);
    }

    /**
     * Test for sending an activation code.
     *
     * @throws Exception              if a general error occurs.
     * @throws EmailNotFoundException if the email is not found.
     */
    @Test
    void testEnviarCodigo() throws Exception, EmailNotFoundException, AccountAlreadyActiveException {
        serviciosCuenta.sendActiveCode("brandone.acevedoc@uqvirtual.edu.co");
    }

    /**
     * Test for sending a password recovery code.
     *
     * @throws Exception              if a general error occurs.
     * @throws EmailNotFoundException if the email is not found.
     */
    @Test
    void testEnviarCodigoRecuperacion() throws Exception, EmailNotFoundException {
        serviciosCuenta.sendPasswordRecoveryCode("brandone.acevedoc@uqvirtual.edu.co");
    }

    /**
     * Test for updating the password.
     *
     * @throws Exception                       if a general error occurs.
     * @throws PasswordMismatchException       if the passwords do not match.
     * @throws InvalidCurrentPasswordException if the current password is invalid.
     */
    @Test
    void testUpdateCode() throws Exception, PasswordMismatchException, InvalidCurrentPasswordException {
        UpdatePasswordDTO updatePasswordDTO = new UpdatePasswordDTO(
                "contraseña1",
                "M@mahermosa123",
                "M@mahermosa123");
        serviciosCuenta.updatePassword(15L, updatePasswordDTO);
    }

    /**
     * Test for changing the password using a code.
     *
     * @throws Exception                      if a general error occurs.
     * @throws PasswordsDoNotMatchException   if the passwords do not match.
     * @throws InvalidValidationCodeException if the validation code is invalid.
     * @throws ValidationCodeExpiredException if the validation code has expired.
     */
    @Test
    void testChangePasswordCode() throws Exception, PasswordsDoNotMatchException, InvalidValidationCodeException,
            ValidationCodeExpiredException {
        ChangePasswordCodeDTO changePasswordCodeDTO = new ChangePasswordCodeDTO(
                "37233",
                "M@mahermosa1234",
                "M@mahermosa1234");
        serviciosCuenta.changePasswordCode(changePasswordCodeDTO);
    }

    @Test
    void testActualizarUsuario() throws Exception, UserNotFoundException {
        // Arrange
        Long accountId = 26L; // ID de una cuenta existente
        ActualizarUsuarioDTO dto = new ActualizarUsuarioDTO(
                "Nuevo Nombre",
                "Nuevo Apellido",
                "3001234562",
                "Nueva Dirección",
                "brandone124@hotmail.com"

        );

        // Act
        String resultado = serviciosCuenta.actualizarUsuario(accountId, dto);

        // Assert
        assertEquals("Usuario actualizado exitosamente.", resultado);
    }

    /**
     * Test method for verifying the functionality of retrieving a user profile.
     *
     * This method tests the `obtenerPerfil` method of the `serviciosCuenta` service
     * to ensure it correctly retrieves the profile information of a user associated
     * with a specific account ID.
     *
     * @throws UserNotFoundException    if the associated user for the account ID is not found.
     * @throws AccountNotFoundException if the account corresponding to the ID is not found.
     */
    @Test
    void testObtenerPerfil() throws UserNotFoundException, AccountNotFoundException {
        // Arrange
        Long accountId = 26L; // ID de una cuenta existente

        // Act
        PerfilDTO perfil = serviciosCuenta.obtenerPerfil(accountId);

        System.out.println(perfil);
    }

}
