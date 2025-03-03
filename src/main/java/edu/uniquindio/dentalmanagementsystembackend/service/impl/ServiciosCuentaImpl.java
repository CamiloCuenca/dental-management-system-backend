package edu.uniquindio.dentalmanagementsystembackend.service.impl;

import edu.uniquindio.dentalmanagementsystembackend.Enum.AccountStatus;
import edu.uniquindio.dentalmanagementsystembackend.Enum.Rol;
import edu.uniquindio.dentalmanagementsystembackend.dto.CrearCuentaDTO;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.Account;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.User;
import edu.uniquindio.dentalmanagementsystembackend.repository.CuentaRepository;
import edu.uniquindio.dentalmanagementsystembackend.repository.UserRepository;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.ServiciosCuenta;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ServiciosCuentaImpl implements ServiciosCuenta {

    private final CuentaRepository accountRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public String crearCuenta(CrearCuentaDTO cuenta) throws Exception {
        // Verificar si ya existe una cuenta con el mismo email.
        if (accountRepository.findByEmail(cuenta.email()).isPresent()) {
           throw new Exception("Email ya existe");
        }

        // Verificar si ya existe una cuenta con el mismo número de identificación.
        if (accountRepository.findByIdUNumber(cuenta.idNumber()).isPresent()) {
            throw new Exception("El usuario ya existe");
        }

        // Encriptar la contraseña
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = bCryptPasswordEncoder.encode(cuenta.password());


        // Crear la cuenta con los datos proporcionados
        Account newAccount = new Account();
        newAccount.setEmail(cuenta.email());
        newAccount.setPassword(hashedPassword);
        newAccount.setRol(Rol.PACIENTE);
        newAccount.setRegistrationDate(LocalDateTime.now());
        newAccount.setStatus(AccountStatus.ACTIVE);
        newAccount.setRegistrationValidationCode(UUID.randomUUID().toString().substring(0,5));
        newAccount.setUser(new User(
                cuenta.idNumber(),
                cuenta.name(),
                cuenta.phoneNumber(),
                cuenta.address(),
                newAccount
        ));

        Account crearAccount = accountRepository.save(newAccount);
        return crearAccount.getId().toString();

    }


}
