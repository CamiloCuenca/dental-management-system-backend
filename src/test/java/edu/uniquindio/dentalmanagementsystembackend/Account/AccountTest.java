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



    @Test
    void testLogin() throws Exception, UserNotFoundException, InvalidPasswordException, AccountInactiveException {
        LoginDTO  loginDTO = new LoginDTO(
                "1001277430",
                "12"
        );

        serviciosCuenta.login(loginDTO);
    }


    @Test
    void testGuardarCuentas() throws Exception, EmailAlreadyExistsException, UserAlreadyExistsException {
        CrearCuentaDTO crearCuentaDTO = new CrearCuentaDTO(
                "1001277430",                            // idNumber
                "Brandon andres",                           // name
                "Acevedo castañeda",                          // lastName
                "3153033412",                        // phoneNumber
                "carrera-15#3",                     // address
                LocalDate.parse("2000-05-20"),      // fechaNacimiento (LocalDate)
                "ba5808864@gmail.com",  // email
                "1234"                 // password
        );
        serviciosCuenta.crearCuenta(crearCuentaDTO);
    }

    @Test
    void testEliminarCuentas() throws Exception, UserNotFoundException, InvalidIdFormatException {
        serviciosCuenta.eliminarCuenta(1L);
    }

    @Test
    void actualizarUsuario() throws Exception, UserNotFoundException, InvalidIdFormatException {
        ActualizarPerfilDTO actualizarPerfilDTO = new ActualizarPerfilDTO("morgan","montealegre","31530331","Maria-cristina#15");
        serviciosCuenta.actualizarPerfil(1L, actualizarPerfilDTO);
    }

    @Test
    void testObtenerUsuario() throws Exception, UserNotFoundException, InvalidIdFormatException {
        serviciosCuenta.obtenerPerfil(1L);
    }

    @Test
    void testActivarCuenta() throws Exception, AccountAlreadyActiveException, ValidationCodeExpiredException {
        ActivateAccountDTO activateAccountDTO = new ActivateAccountDTO(
                "75266",
                "ba5808864@gmail.com"
        );
        serviciosCuenta.activateAccount(activateAccountDTO);
    }

    @Test
    void testEnviarCodigo() throws Exception, EmailNotFoundException {
        serviciosCuenta.sendActiveCode("ba5808864@gmail.com");
    }


    @Test
    void testUpdate() throws Exception, PasswordMismatchException, InvalidCurrentPasswordException {
        UpdatePasswordDTO updatePasswordDTO = new UpdatePasswordDTO(
                "12345",
                "12",
                "12"
        );
        serviciosCuenta.updatePassword(4L,updatePasswordDTO);
    }



}
