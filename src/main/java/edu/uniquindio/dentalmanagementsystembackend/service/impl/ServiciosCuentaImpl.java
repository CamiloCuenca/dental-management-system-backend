package edu.uniquindio.dentalmanagementsystembackend.service.impl;

import edu.uniquindio.dentalmanagementsystembackend.Enum.AccountStatus;
import edu.uniquindio.dentalmanagementsystembackend.Enum.Rol;
import edu.uniquindio.dentalmanagementsystembackend.dto.ActualizarPerfilDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.CrearCuentaDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.PerfilDTO;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.Account;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.User;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.ValidationCode;
import edu.uniquindio.dentalmanagementsystembackend.repository.CuentaRepository;
import edu.uniquindio.dentalmanagementsystembackend.repository.UserRepository;
import edu.uniquindio.dentalmanagementsystembackend.repository.validationCodeRepository;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.EmailService;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.ServiciosCuenta;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ServiciosCuentaImpl implements ServiciosCuenta {

    private final CuentaRepository accountRepository;
    private final UserRepository userRepository;
    private final validationCodeRepository validationCodeRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;




    @Override
    @Transactional
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
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(cuenta.password());

        // Crear la cuenta con los datos proporcionados
        Account newAccount = new Account();
        newAccount.setEmail(cuenta.email());
        newAccount.setPassword(hashedPassword);
        newAccount.setRol(Rol.PACIENTE);
        newAccount.setRegistrationDate(LocalDateTime.now());
        newAccount.setStatus(AccountStatus.INACTIVE);

        // Crear y asignar el usuario
        User newUser = new User(
                cuenta.idNumber(),
                cuenta.name(),
                cuenta.lastName(),
                cuenta.phoneNumber(),
                cuenta.address(),
                cuenta.fechaNacimiento(),
                newAccount
        );
        newAccount.setUser(newUser);

        // Generar código de activación
        newAccount.setRegistrationValidationCode(new ValidationCode(generateValidationCode()));

        // Guardar la cuenta en la base de datos
        Account createdAccount = accountRepository.save(newAccount);

        // Enviar el código por email
        emailService.sendCodevalidation(createdAccount.getEmail(), createdAccount.getRegistrationValidationCode().getCode());

        return createdAccount.getId().toString();
    }

    private String generateValidationCode() {
        return String.format("%05d", new SecureRandom().nextInt(100000));
    }





    @Override
    public PerfilDTO obtenerPerfil(String idNumber) throws Exception {
        Optional<User> userOptional = userRepository.findById(Long.valueOf(idNumber));

        if (userOptional.isEmpty()) {
            throw new Exception("El usuario con ID " + idNumber + " no existe.");
        }

        User user = userOptional.get();
        return new PerfilDTO(
                user.getIdNumber(),
                user.getName(),
                user.getLastName(),
                user.getPhoneNumber(),
                user.getAddress(),
                user.getBirthDate(),
                user.getAccount().getEmail()
        );
    }

    @Override
    public void actualizarPerfil(String idNumber, ActualizarPerfilDTO actualizarPerfilDTO) throws Exception {
        Optional<User> userOptional = userRepository.findById(Long.valueOf(idNumber));

        if (userOptional.isEmpty()) {
            throw new Exception("El usuario con ID " + idNumber + " no existe.");
        }

        User user = userOptional.get();
        user.setName(actualizarPerfilDTO.name());
        user.setPhoneNumber(actualizarPerfilDTO.phoneNumber());
        user.setAddress(actualizarPerfilDTO.address());

        userRepository.save(user);

    }

    @Override
    public void eliminarCuenta(String idNumber) throws Exception {

        Optional<User> userOptional = userRepository.findById(Long.valueOf(idNumber));

        if (userOptional.isEmpty()) {
            throw new Exception("El usuario con ID " + idNumber + " no existe.");
        }

        User user = userOptional.get();
        user.getAccount().setStatus(AccountStatus.INACTIVE);

        userRepository.save(user);

    }


}
