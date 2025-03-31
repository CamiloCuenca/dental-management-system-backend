package edu.uniquindio.dentalmanagementsystembackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class DentalManagementSystemBackendApplication {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Lista de contraseñas en texto plano
        String[] rawPasswords = {
                "contraseña1", "contraseña2", "contraseña3",
                "contraseña4", "contraseña5", "contraseña6",
                "contraseña7", "contraseña8", "contraseña9",
                "contraseña10"
        };

        // Generar y mostrar contraseñas encriptadas
        for (String password : rawPasswords) {
            System.out.println(password + " -> " + encoder.encode(password));
        }
    }



}
