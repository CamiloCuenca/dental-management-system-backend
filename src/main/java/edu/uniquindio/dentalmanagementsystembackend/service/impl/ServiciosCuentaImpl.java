package edu.uniquindio.dentalmanagementsystembackend.service.impl;

import edu.uniquindio.dentalmanagementsystembackend.Enum.AccountStatus;
import edu.uniquindio.dentalmanagementsystembackend.Enum.Rol;

import edu.uniquindio.dentalmanagementsystembackend.config.JWTUtils;
import edu.uniquindio.dentalmanagementsystembackend.dto.JWT.TokenDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.account.*;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.Account;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.RecoveryCode;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.User;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.ValidationCode;
import edu.uniquindio.dentalmanagementsystembackend.exception.*;
import edu.uniquindio.dentalmanagementsystembackend.repository.CuentaRepository;
import edu.uniquindio.dentalmanagementsystembackend.repository.RecoveryCodeRepository;
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
    private final RecoveryCodeRepository recoveryCode;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JWTUtils jwtUtils;
    private final RecoveryCodeRepository recoveryCodeRepository;


    private Map<String, Object> construirClaims(Account account) {
        return Map.of(
                "rol", account.getRol(),
                "nombre", account.getUser().getName(),
                "id", account.getId(),
                "idUser", account.getUser().getIdNumber(),
                "email", account.getEmail()
        );
    }


    /**
     * Inicia sesión en el sistema.
     * @param loginDTO DTO con las credenciales de inicio de sesión.
     * @return TokenDTO con el token de autenticación.
     * @throws UserNotFoundException si el usuario no se encuentra.
     * @throws AccountInactiveException si la cuenta está inactiva.
     * @throws InvalidPasswordException si la contraseña es incorrecta.
     */
    @Override
    public TokenDTO login(LoginDTO loginDTO) throws UserNotFoundException, AccountInactiveException, InvalidPasswordException {
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

    /**
     * Crea una nueva cuenta de usuario.
     * @param cuenta DTO con la información de la cuenta a crear.
     * @return String con un mensaje de confirmación.
     * @throws EmailAlreadyExistsException si el correo electrónico ya está registrado.
     * @throws UserAlreadyExistsException si el usuario ya existe.
     * @throws Exception si ocurre un error general.
     */
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

    /**
     * Genera un código de validación.
     * @return String con el código de validación generado.
     */
    private String generateValidationCode() {
        return String.format("%05d", new SecureRandom().nextInt(100000));
    }

    /**
     * Obtiene el perfil del paciente basado en su identificación.
     * @param accountId Número de identificación del paciente.
     * @return PerfilDTO con la información del usuario.
     * @throws UserNotFoundException si el usuario no existe.
     */
    @Override
    public PerfilDTO obtenerPerfil(Long accountId) throws UserNotFoundException {
        // Buscar la cuenta en la base de datos
        Optional<Account> accountOptional = accountRepository.findById(accountId);

        if (accountOptional.isEmpty()) {
            throw new UserNotFoundException("No se encontró una cuenta con ID " + accountId);
        }

        Account account = accountOptional.get();
        User user = account.getUser(); // Obtener el usuario asociado a la cuenta

        // Verificar si la cuenta tiene un usuario asociado
        if (user == null) {
            throw new UserNotFoundException("La cuenta con ID " + accountId + " no tiene un usuario asociado.");
        }

        // Retornar los datos en el DTO
        return new PerfilDTO(
                user.getIdNumber(),
                user.getName(),
                user.getLastName(),
                user.getPhoneNumber(),
                user.getAddress(),
                user.getBirthDate(),
                account.getEmail() // Ahora tomamos el email desde la cuenta
        );
    }

    /**
     * Actualiza los datos personales del usuario.
     * @param accountId Número de identificación del usuario.
     * @param actualizarPerfilDTO DTO con los datos a actualizar.
     * @throws UserNotFoundException si el usuario no existe.
     */
    @Override
    @Transactional
    public void actualizarPerfil(Long accountId, ActualizarPerfilDTO actualizarPerfilDTO)
            throws UserNotFoundException {

        // Buscar la cuenta por su ID
        Optional<Account> accountOptional = accountRepository.findById(accountId);

        if (accountOptional.isEmpty()) {
            throw new UserNotFoundException("La cuenta con ID " + accountId + " no existe.");
        }

        // Obtener la cuenta y verificar si tiene un usuario asociado
        Account account = accountOptional.get();
        User user = account.getUser();

        if (user == null) {
            throw new UserNotFoundException("No se encontró un usuario asociado a la cuenta con ID " + accountId);
        }

        // Actualizar los datos del usuario
        user.setName(actualizarPerfilDTO.name());
        user.setLastName(actualizarPerfilDTO.lastName());
        user.setPhoneNumber(actualizarPerfilDTO.phoneNumber());
        user.setAddress(actualizarPerfilDTO.address());

        // Guardar los cambios en el usuario
        userRepository.save(user);
    }

    /**
     * Desactiva la cuenta del usuario.
     * @param accountId Número de identificación del usuario.
     * @throws AccountNotFoundException si la cuenta no existe.
     */
    @Override
    @Transactional
    public void eliminarCuenta(Long accountId) throws AccountNotFoundException {

        // Buscar la cuenta en la base de datos
        Optional<Account> accountOptional = accountRepository.findById(accountId);

        if (accountOptional.isEmpty()) {
            throw new AccountNotFoundException("No se encontró una cuenta con ID " + accountId);
        }

        Account account = accountOptional.get();

        // Cambiar el estado de la cuenta a INACTIVE en lugar de eliminarla
        account.setStatus(AccountStatus.INACTIVE);

        // Guardar cambios en la base de datos
        accountRepository.save(account);
    }

    /**
     * Activa la cuenta del usuario.
     * @param activateAccountDTO DTO con la información para activar la cuenta.
     * @return String con un mensaje de confirmación.
     * @throws AccountAlreadyActiveException si la cuenta ya está activa.
     * @throws ValidationCodeExpiredException si el código de validación ha expirado.
     * @throws AccountNotFoundException si la cuenta no se encuentra.
     */
    @Override
    @Transactional
    public String activateAccount(ActivateAccountDTO activateAccountDTO)
            throws AccountAlreadyActiveException, ValidationCodeExpiredException, AccountNotFoundException {

        // Buscar la cuenta por código de activación usando JPA
        Account account = accountRepository.findByRegistrationValidationCode_Code(activateAccountDTO.code())
                .orElseThrow(() -> new AccountNotFoundException("No se encontró una cuenta con el código: " + activateAccountDTO.code()));

        // Verificar si la cuenta ya está activa
        if (account.getStatus() == AccountStatus.ACTIVE) {
            throw new AccountAlreadyActiveException("La cuenta ya está activada.");
        }

        // Obtener y verificar el código de validación asociado a la cuenta
        ValidationCode validationCode = Optional.ofNullable(account.getRegistrationValidationCode())
                .orElseThrow(() -> new ValidationCodeExpiredException("El código de validación no existe."));

        // Validar si el código ha expirado
        if (validationCode.isExpired()) {
            throw new ValidationCodeExpiredException("El código de validación ha expirado.");
        }

        // **Eliminar el código de activación de la base de datos**
        account.setRegistrationValidationCode(null); // Desvincular la relación en la entidad Account
        validationCodeRepository.delete(validationCode); // Eliminar la entidad ValidationCode de la BD

        // Activar la cuenta
        account.setStatus(AccountStatus.ACTIVE);
        accountRepository.save(account); // Guardar cambios en la base de datos

        return "Cuenta activada exitosamente.";
    }

    /**
     * Envía un código de activación al correo electrónico del usuario.
     * @param email Correo electrónico del usuario.
     * @return String con un mensaje de confirmación.
     * @throws EmailNotFoundException si el correo electrónico no se encuentra.
     * @throws Exception si ocurre un error general.
     */
    @Override
    @Transactional
    public String sendActiveCode(String email) throws Exception, EmailNotFoundException {
        // Buscar la cuenta por email
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException(email));

        // Si ya tiene un código de activación previo, eliminarlo
        ValidationCode existingCode = account.getRegistrationValidationCode();
        if (existingCode != null) {
            validationCodeRepository.delete(existingCode);
        }

        // Generar código de activación
        ValidationCode validationCode = new ValidationCode();
        validationCode.setCode(generateValidationCode());

        // Asignar el nuevo código a la cuenta
        account.setRegistrationValidationCode(validationCode);
        accountRepository.save(account); // Guardar los cambios en la cuenta

        // Enviar el código por correo
        emailService.sendCodevalidation(account.getEmail(), account.getRegistrationValidationCode().getCode());

        return "Código de validación de cuenta enviado al correo: " + account.getEmail();
    }

    /**
     * Cambia el código de la contraseña.
     * @param changePasswordDTO DTO con la información para cambiar el código de la contraseña.
     * @return String con un mensaje de confirmación.
     * @throws InvalidValidationCodeException si el código de validación es inválido.
     * @throws ValidationCodeExpiredException si el código de validación ha expirado.
     * @throws PasswordsDoNotMatchException si las contraseñas no coinciden.
     * @throws Exception si ocurre un error general.
     */
    @Override
    @Transactional
    public String changePasswordCode(ChangePasswordCodeDTO changePasswordDTO) throws Exception, InvalidValidationCodeException, ValidationCodeExpiredException, PasswordsDoNotMatchException {
        // Buscar la cuenta del usuario por el código de validación
        Account account = accountRepository.findByRecoveryCode_Code(changePasswordDTO.code())
                .orElseThrow(() -> new InvalidValidationCodeException("No se encontró la cuenta."));

        RecoveryCode recoveryCode = account.getRecoveryCode();

        // Verificar si el código de recuperación es nulo o ha expirado
        if (recoveryCode == null || recoveryCode.isExpired()) {
            throw new ValidationCodeExpiredException("El código de recuperación ha expirado o no es válido.");
        }

        // Verificar que las contraseñas coincidan
        if (!changePasswordDTO.newPassword().equals(changePasswordDTO.confirmationPassword())) {
            throw new PasswordsDoNotMatchException("Las contraseñas no coinciden.");
        }

        // Encriptar la nueva contraseña y actualizar la cuenta
        account.setPassword(passwordEncoder.encode(changePasswordDTO.newPassword()));

        // Eliminar el código de recuperación después de cambiar la contraseña exitosamente
        recoveryCodeRepository.delete(recoveryCode);
        account.setRecoveryCode(null);
        // Guardar la cuenta actualizada en el repositorio
        accountRepository.save(account);

        return "La contraseña ha sido cambiada exitosamente.";
    }

    /**
     * Actualiza la contraseña del usuario.
     * @param id Número de identificación del usuario.
     * @param updatePasswordDTO DTO con la nueva contraseña.
     * @return String con un mensaje de confirmación.
     * @throws AccountNotFoundException si la cuenta no se encuentra.
     * @throws InvalidCurrentPasswordException si la contraseña actual es incorrecta.
     * @throws PasswordMismatchException si las contraseñas no coinciden.
     */
    @Override
    @Transactional
    public String updatePassword(Long id, UpdatePasswordDTO updatePasswordDTO)
            throws AccountNotFoundException, InvalidCurrentPasswordException, PasswordMismatchException {

        // Buscar la cuenta en la base de datos utilizando el ID proporcionado
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("No se encontró la cuenta con ID: " + id));

        // Verificar si la contraseña actual coincide con la almacenada
        if (!passwordEncoder.matches(updatePasswordDTO.currentPassword(), account.getPassword())) {
            throw new InvalidCurrentPasswordException("La contraseña actual es incorrecta.");
        }

        // Obtener y validar la nueva contraseña y su confirmación
        String newPassword = updatePasswordDTO.newPassword();
        String confirmNewPassword = updatePasswordDTO.confirmationPassword();

        if (!newPassword.equals(confirmNewPassword)) {
            throw new PasswordMismatchException("La nueva contraseña y la confirmación no coinciden.");
        }

        // Encriptar y actualizar la nueva contraseña
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);

        return "La contraseña ha sido cambiada exitosamente.";
    }

    /**
     * Envía un código de recuperación de contraseña al correo electrónico del usuario.
     * @param email Correo electrónico del usuario.
     * @return String con un mensaje de confirmación.
     * @throws EmailNotFoundException si el correo electrónico no se encuentra.
     * @throws Exception si ocurre un error general.
     */
    @Override
    @Transactional
    public String sendPasswordRecoveryCode(String email) throws Exception, EmailNotFoundException {
        // Buscar la cuenta en la base de datos por email
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("No se encontró una cuenta asociada al email: " + email));

        // Si ya tiene un código de recuperación previo, eliminarlo
        RecoveryCode existingCode = account.getRecoveryCode();
        if (existingCode != null) {
            recoveryCode.delete(existingCode);
        }

        // Generar código de activación
        RecoveryCode recoveryCode = new RecoveryCode();
        recoveryCode.setCode(generateValidationCode());

        // Asignar el nuevo código a la cuenta
        account.setRecoveryCode(recoveryCode);
        accountRepository.save(account); // Guardar los cambios en la cuenta

        // Enviar el código por correo
        emailService.sendRecoveryCode(account.getEmail(), account.getRecoveryCode().getCode());

        return "Código de recuperacion de contraseña enviado al correo: " + account.getEmail();
    }


}
