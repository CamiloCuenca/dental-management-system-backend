package edu.uniquindio.dentalmanagementsystembackend.Account;

import edu.uniquindio.dentalmanagementsystembackend.dto.account.*;
import edu.uniquindio.dentalmanagementsystembackend.exception.*;
import edu.uniquindio.dentalmanagementsystembackend.repository.CuentaRepository;
import edu.uniquindio.dentalmanagementsystembackend.repository.UserRepository;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.ServiciosCuenta;
import edu.uniquindio.dentalmanagementsystembackend.exception.ValidationCodeExpiredException;
import edu.uniquindio.dentalmanagementsystembackend.exception.InvalidCurrentPasswordException;
import edu.uniquindio.dentalmanagementsystembackend.exception.PasswordMismatchException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class AccountTest {


    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    ServiciosCuenta serviciosCuenta;



    /**
     * Test for user login.
     * @throws Exception if a general error occurs.
     * @throws UserNotFoundException if the user is not found.
     * @throws InvalidPasswordException if the password is invalid.
     * @throws AccountInactiveException if the account is inactive.
     */
    @Test
    void testLogin() throws Exception, UserNotFoundException, InvalidPasswordException, AccountInactiveException {
        LoginDTO loginDTO = new LoginDTO(
                "1001277430",
                "1234"
        );

        serviciosCuenta.login(loginDTO);
    }

    /**
     * Test for creating an account.
     * @throws Exception if a general error occurs.
     * @throws EmailAlreadyExistsException if the email already exists.
     * @throws UserAlreadyExistsException if the user already exists.
     */
    @Test
    void testGuardarCuentas() throws Exception, EmailAlreadyExistsException, UserAlreadyExistsException {
        CrearCuentaDTO crearCuentaDTO = new CrearCuentaDTO(
                "1001277431",                            // idNumber
                "Brandon algo",                        // name
                "Acevedo castañeda",                     // lastName
                "3153033414",                            // phoneNumber
                "carrera-15#3",                          // address
                LocalDate.parse("2000-05-20"),           // fechaNacimiento (LocalDate)
                "doctor123@gmail.com",                   // email
                "1234"                                   // password
        );
        serviciosCuenta.crearCuenta(crearCuentaDTO);
    }

    /**
     * Test for deleting an account.
     * @throws Exception if a general error occurs.
     * @throws UserNotFoundException if the user is not found.
     * @throws InvalidIdFormatException if the ID format is invalid.
     */
    @Test
    void testEliminarCuentas() throws Exception, UserNotFoundException, InvalidIdFormatException {
        serviciosCuenta.eliminarCuenta(1L);
    }

    /**
     * Test for updating a user profile.
     * @throws Exception if a general error occurs.
     * @throws UserNotFoundException if the user is not found.
     * @throws InvalidIdFormatException if the ID format is invalid.
     */
    @Test
    void actualizarUsuario() throws Exception, UserNotFoundException, InvalidIdFormatException {
        ActualizarPerfilDTO actualizarPerfilDTO = new ActualizarPerfilDTO("morgan", "montealegre", "31530331", "Maria-cristina#15");
        serviciosCuenta.actualizarPerfil(1L, actualizarPerfilDTO);
    }

    /**
     * Test for obtaining a user profile.
     * @throws Exception if a general error occurs.
     * @throws UserNotFoundException if the user is not found.
     * @throws InvalidIdFormatException if the ID format is invalid.
     */
    @Test
    void testObtenerUsuario() throws Exception, UserNotFoundException, InvalidIdFormatException {
        serviciosCuenta.obtenerPerfil(1L);
    }

    /**
     * Test for activating an account.
     * @throws Exception if a general error occurs.
     * @throws AccountAlreadyActiveException if the account is already active.
     * @throws ValidationCodeExpiredException if the validation code has expired.
     */
    @Test
    void testActivarCuenta() throws Exception, AccountAlreadyActiveException, ValidationCodeExpiredException {
        ActivateAccountDTO activateAccountDTO = new ActivateAccountDTO(
                "22234",
                "doctor123@gmail.com"
        );
        serviciosCuenta.activateAccount(activateAccountDTO);
    }

    /**
     * Test for sending an activation code.
     * @throws Exception if a general error occurs.
     * @throws EmailNotFoundException if the email is not found.
     */
    @Test
    void testEnviarCodigo() throws Exception, EmailNotFoundException {
        serviciosCuenta.sendActiveCode("ba5808864@gmail.com");
    }

    /**
     * Test for sending a password recovery code.
     * @throws Exception if a general error occurs.
     * @throws EmailNotFoundException if the email is not found.
     */
    @Test
    void testEnviarCodigoRecuperacion() throws Exception, EmailNotFoundException {
        serviciosCuenta.sendPasswordRecoveryCode("brandone.acevedoc@uqvirtual.edu.co");
    }

    /**
     * Test for updating the password.
     * @throws Exception if a general error occurs.
     * @throws PasswordMismatchException if the passwords do not match.
     * @throws InvalidCurrentPasswordException if the current password is invalid.
     */
    @Test
    void testUpdateCode() throws Exception, PasswordMismatchException, InvalidCurrentPasswordException {
        UpdatePasswordDTO updatePasswordDTO = new UpdatePasswordDTO(
                "12345",
                "12",
                "12"
        );
        serviciosCuenta.updatePassword(4L, updatePasswordDTO);
    }

    /**
     * Test for changing the password using a code.
     * @throws Exception if a general error occurs.
     * @throws PasswordsDoNotMatchException if the passwords do not match.
     * @throws InvalidValidationCodeException if the validation code is invalid.
     * @throws ValidationCodeExpiredException if the validation code has expired.
     */
    @Test
    void testChangePasswordCode() throws Exception, PasswordsDoNotMatchException, InvalidValidationCodeException, ValidationCodeExpiredException {
        ChangePasswordCodeDTO changePasswordCodeDTO = new ChangePasswordCodeDTO(
                "59473",
                "123",
                "123"
        );
        serviciosCuenta.changePasswordCode(changePasswordCodeDTO);
    }



}
