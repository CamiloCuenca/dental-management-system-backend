package edu.uniquindio.dentalmanagementsystembackend.Account;

import edu.uniquindio.dentalmanagementsystembackend.Enum.AccountStatus;
import edu.uniquindio.dentalmanagementsystembackend.Enum.Rol;
import edu.uniquindio.dentalmanagementsystembackend.dto.account.ActualizarPerfilDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.account.CrearCuentaDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.account.LoginDTO;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.Account;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.User;
import edu.uniquindio.dentalmanagementsystembackend.repository.CuentaRepository;
import edu.uniquindio.dentalmanagementsystembackend.repository.UserRepository;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.ServiciosCuenta;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

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
    void testLogin() throws Exception {
        LoginDTO  loginDTO = new LoginDTO(
                "12345",
                "4321"
        );

        serviciosCuenta.login(loginDTO);
    }


    @Test
    void testGuardarCuentas() throws Exception {
        CrearCuentaDTO crearCuentaDTO = new CrearCuentaDTO(
                "12345",                            // idNumber
                "Brandon andres",                           // name
                "Acevedo castañeda",                          // lastName
                "123",                        // phoneNumber
                "carrera-15#3",                     // address
                LocalDate.parse("2000-05-20"),      // fechaNacimiento (LocalDate)
                "brndon@gmail.com",  // email
                "4321"                 // password
        );
        serviciosCuenta.crearCuenta(crearCuentaDTO);
    }

    @Test
    void testEliminarCuentas() throws Exception {
        serviciosCuenta.eliminarCuenta("1234");
    }

    @Test
    void actualizarUsuario() throws Exception {
        ActualizarPerfilDTO actualizarPerfilDTO = new ActualizarPerfilDTO("Brandon","montealegre","31530331","Maria-cristina#15");
        serviciosCuenta.actualizarPerfil("1234", actualizarPerfilDTO);
    }


}
