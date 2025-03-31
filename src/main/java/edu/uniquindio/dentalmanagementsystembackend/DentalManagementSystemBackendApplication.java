package edu.uniquindio.dentalmanagementsystembackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class DentalManagementSystemBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(DentalManagementSystemBackendApplication.class, args);
    }



}
