package edu.uniquindio.dentalmanagementsystembackend.service.impl;

import edu.uniquindio.dentalmanagementsystembackend.Enum.AccountStatus;
import edu.uniquindio.dentalmanagementsystembackend.Enum.Rol;

import edu.uniquindio.dentalmanagementsystembackend.config.JWTUtils;
import edu.uniquindio.dentalmanagementsystembackend.dto.JWT.TokenDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.account.ActualizarPerfilDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.account.CrearCuentaDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.account.LoginDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.account.PerfilDTO;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.Account;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.User;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.ValidationCode;
import edu.uniquindio.dentalmanagementsystembackend.exception.*;
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

import javax.security.auth.login.AccountNotFoundException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ServiciosCuentaImpl implements ServiciosCuenta {

    private final CuentaRepository accountRepository;
    private final UserRepository userRepository;
    private final validationCodeRepository validationCodeRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JWTUtils jwtUtils;


    private Map<String, Object> construirClaims(Account account) {
        return Map.of(
                "rol", account.getRol(),
                "nombre", account.getUser().getName(),
                "id", account.getId(),
                "email", account.getEmail()
        );
    }


    @Override
    public TokenDTO login(LoginDTO loginDTO) throws UserNotFoundException, AccountInactiveException, InvalidPasswordException{
        // Buscar la cuenta por el número de identificación (cédula)
        Optional<Account> accountOptional = accountRepository.findByIdUNumber(String.valueOf(loginDTO.idNumber()));

        if (accountOptional.isEmpty()) {
            throw new UserNotFoundException("Usuario con ID " + loginDTO.idNumber() + " no encontrado.");
        }

        Account account = accountOptional.get();

        // Verificar si la cuenta está activa
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountInactiveException("La cuenta no está activa.");
        }

        // Comparar la contraseña ingresada con la almacenada en la base de datos
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(loginDTO.password(), account.getPassword())) {
            throw new InvalidPasswordException("Contraseña incorrecta.");
        }

        // Construir claims para el token
        Map<String, Object> map = construirClaims(account);

        // Generar el token JWT
        String token = jwtUtils.generateToken(account.getEmail(), map);

        // 🔹 Imprimir el token en la consola
        System.out.println("🔑 Token generado: " + token);

        // Retornar el token de autenticación
        return new TokenDTO(token);
    }

    @Override
    @Transactional
    public String crearCuenta(CrearCuentaDTO cuenta) throws EmailAlreadyExistsException, UserAlreadyExistsException, Exception {
        // Verificar si ya existe una cuenta con el mismo email.
        if (accountRepository.findByEmail(cuenta.email()).isPresent()) {
            throw new EmailAlreadyExistsException("El email " + cuenta.email() + " ya está registrado.");
        }

        // Verificar si ya existe un usuario con el mismo número de identificación.
        if (userRepository.existsByIdNumber(cuenta.idNumber())) {
            throw new UserAlreadyExistsException("El usuario con ID " + cuenta.idNumber() + " ya existe.");
        }

        // Encriptar la contraseña
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(cuenta.password());

        // Crear la cuenta
        Account newAccount = new Account();
        newAccount.setEmail(cuenta.email());
        newAccount.setPassword(hashedPassword);
        newAccount.setRol(Rol.PACIENTE);
        newAccount.setStatus(AccountStatus.INACTIVE);

        // Generar código de activación
        ValidationCode validationCode = new ValidationCode();
        validationCode.setCode(generateValidationCode());
        newAccount.setRegistrationValidationCode(validationCode);

        // Crear usuario
        User newUser = new User();
        newUser.setIdNumber(cuenta.idNumber());
        newUser.setName(cuenta.name());
        newUser.setLastName(cuenta.lastName());
        newUser.setPhoneNumber(cuenta.phoneNumber());
        newUser.setAddress(cuenta.address());
        newUser.setBirthDate(cuenta.fechaNacimiento());

        // Relacionar usuario con cuenta
        newUser.setAccount(newAccount);
        newAccount.setUser(newUser);

        // Guardar la cuenta (también guardará el usuario por `CascadeType.ALL`)
        Account createdAccount = accountRepository.save(newAccount);

        // Enviar código de validación por email
        emailService.sendCodevalidation(createdAccount.getEmail(), createdAccount.getRegistrationValidationCode().getCode());

        return createdAccount.getId().toString();
    }

    private String generateValidationCode() {
        return String.format("%05d", new SecureRandom().nextInt(100000));
    }





    @Override
    public PerfilDTO obtenerPerfil(String idNumber) throws UserNotFoundException, InvalidIdFormatException {
        // Intentar convertir el idNumber a Long
        Long userId;
        try {
            userId = Long.valueOf(idNumber);
        } catch (NumberFormatException e) {
            throw new InvalidIdFormatException("El ID proporcionado no es válido: " + idNumber);
        }

        // Buscar al usuario en la base de datos
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("El usuario con ID " + idNumber + " no existe.");
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
    @Transactional
    public void actualizarPerfil(String idNumber, ActualizarPerfilDTO actualizarPerfilDTO)
            throws UserNotFoundException, InvalidIdFormatException {

        // Validar que el idNumber sea un número válido
        Long userId;
        try {
            userId = Long.valueOf(idNumber);
        } catch (NumberFormatException e) {
            throw new InvalidIdFormatException("El ID proporcionado no es válido: " + idNumber);
        }

        // Buscar el usuario en la base de datos
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("El usuario con ID " + idNumber + " no existe.");
        }

        // Obtener el usuario y actualizar sus datos
        User user = userOptional.get();
        user.setName(actualizarPerfilDTO.name());
        user.setLastName(actualizarPerfilDTO.lastName());
        user.setPhoneNumber(actualizarPerfilDTO.phoneNumber());
        user.setAddress(actualizarPerfilDTO.address());

        // Guardar cambios en la base de datos
        userRepository.save(user);
    }



    @Override
    @Transactional
    public void eliminarCuenta(String idNumber)
            throws UserNotFoundException, InvalidIdFormatException, AccountNotFoundException {

        // Validar que el idNumber sea un número válido
        Long userId;
        try {
            userId = Long.valueOf(idNumber);
        } catch (NumberFormatException e) {
            throw new InvalidIdFormatException("El ID proporcionado no es válido: " + idNumber);
        }

        // Buscar el usuario por su ID
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("El usuario con ID " + idNumber + " no existe.");
        }

        User user = userOptional.get();

        // Verificar si el usuario tiene una cuenta asociada
        if (user.getAccount() == null) {
            throw new AccountNotFoundException("El usuario con ID " + idNumber + " no tiene una cuenta asociada.");
        }

        // Cambiar el estado de la cuenta a INACTIVE en lugar de eliminarla
        Account account = user.getAccount();
        account.setStatus(AccountStatus.INACTIVE);

        // Guardar cambios en la base de datos
        accountRepository.save(account);
    }


}
