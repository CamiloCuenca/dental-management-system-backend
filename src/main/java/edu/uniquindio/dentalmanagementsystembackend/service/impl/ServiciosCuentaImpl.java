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
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.AccountNotFoundException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
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
     *
     * @param loginDTO DTO con las credenciales de inicio de sesión.
     * @return TokenDTO con el token de autenticación.
     * @throws UserNotFoundException    si el usuario no se encuentra.
     * @throws AccountInactiveException si la cuenta está inactiva.
     * @throws InvalidPasswordException si la contraseña es incorrecta.
     */
    @Override
    @Transactional
    public TokenDTO login(LoginDTO loginDTO)
            throws UserNotFoundException, AccountInactiveException, InvalidPasswordException {

        validarLoginDTO(loginDTO);
        String idNumber = loginDTO.idNumber().trim();

        Optional<Account> accountOptional = accountRepository.findByIdUNumber(idNumber);

        if (accountOptional.isEmpty()) {
            throw new UserNotFoundException("Usuario con ID " + idNumber + " no encontrado.");
        }

        Account account = accountOptional.get();

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountInactiveException("La cuenta no está activa.");
        }

        if (!passwordEncoder.matches(loginDTO.password(), account.getPassword())) {
            throw new InvalidPasswordException("Contraseña incorrecta.");
        }

        // 🛠 Generar el token
        Map<String, Object> claims = construirClaims(account);
        String token = jwtUtils.generateToken(account.getEmail(), claims);

        return new TokenDTO(token);
    }

    private void validarLoginDTO(LoginDTO loginDTO) {
        if (loginDTO == null) {
            throw new IllegalArgumentException("El objeto LoginDTO no puede ser nulo.");
        }
        if (loginDTO.idNumber() == null || loginDTO.idNumber().isBlank()) {
            throw new IllegalArgumentException("El número de identificación no puede estar vacío.");
        }
        if (loginDTO.password() == null || loginDTO.password().isBlank()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía.");
        }
    }

    /**
     * Crea una nueva cuenta de usuario.
     *
     * @param cuenta DTO con la información de la cuenta a crear.
     * @return String con un mensaje de confirmación.
     * @throws EmailAlreadyExistsException si el correo electrónico ya está registrado.
     * @throws UserAlreadyExistsException  si el usuario ya existe.
     * @throws Exception                   si ocurre un error general.
     */
    @Override
    @Transactional
    public String crearCuenta(CrearCuentaDTO cuenta)
            throws EmailAlreadyExistsException, UserAlreadyExistsException, DatabaseOperationException, EmailSendingException {

        // Validar los datos del DTO de creación de cuenta
        validarCrearCuentaDTO(cuenta);

        // Verificar si el email ya está registrado
        if (accountRepository.findByEmail(cuenta.email()).isPresent()) {
            throw new EmailAlreadyExistsException("El email " + cuenta.email() + " ya está registrado.");
        }

        // Verificar si el ID ya está registrado
        if (userRepository.existsByIdNumber(cuenta.idNumber())) {
            throw new UserAlreadyExistsException("El usuario con ID " + cuenta.idNumber() + " ya existe.");
        }

        try {
            // Encriptar la contraseña
            String hashedPassword = passwordEncoder.encode(cuenta.password());

            // Crear cuenta y usuario
            Account newAccount = construirCuenta(cuenta, hashedPassword);
            User newUser = construirUsuario(cuenta, newAccount);
            newAccount.setUser(newUser);

            // Guardar en la base de datos
            Account createdAccount = accountRepository.save(newAccount);

            // Enviar código de validación por email
            enviarCodigoValidacion(createdAccount);

            return createdAccount.getId().toString();

        } catch (DataAccessException dae) {
            throw new DatabaseOperationException("Error al acceder a la base de datos: " + dae.getMessage());
        } catch (EmailSendingException ese) {
            throw new EmailSendingException("Error al enviar el correo de validación: " + ese.getMessage());
        } catch (Exception e) {
            throw new DatabaseOperationException("Error inesperado al crear la cuenta: " + e.getMessage());
        }
    }

    /**
     * Valida los datos del DTO de creación de cuenta.
     *
     * @param cuenta DTO con la información de la cuenta a crear.
     * @throws IllegalArgumentException si algún dato es nulo o vacío.
     */
    private void validarCrearCuentaDTO(CrearCuentaDTO cuenta) {
        if (cuenta == null) {
            throw new IllegalArgumentException("El objeto CrearCuentaDTO no puede ser nulo.");
        }
        if (cuenta.idNumber() == null || cuenta.idNumber().isBlank()) {
            throw new IllegalArgumentException("El número de identificación no puede estar vacío.");
        }
        if (cuenta.email() == null || cuenta.email().isBlank()) {
            throw new IllegalArgumentException("El email no puede estar vacío.");
        }
        if (cuenta.password() == null || cuenta.password().isBlank()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía.");
        }
    }

    /**
     * Envía un código de validación al correo electrónico del usuario.
     *
     * @param createdAccount La cuenta recién creada.
     * @throws EmailSendingException si ocurre un error al enviar el correo.
     */
    private void enviarCodigoValidacion(Account createdAccount) throws EmailSendingException {
        if (createdAccount.getEmail() == null) {
            throw new EmailSendingException("El email de la cuenta es nulo. No se puede enviar el código de validación.");
        }

        try {
            emailService.sendCodevalidation(
                    createdAccount.getEmail(),
                    createdAccount.getRegistrationValidationCode().getCode()
            );
        } catch (Exception e) {
            throw new EmailSendingException("Error al enviar el correo de validación: " + e.getMessage());
        }
    }

    /**
     * Construye y devuelve una nueva cuenta con los datos proporcionados.
     *
     * @param cuenta DTO con la información de la cuenta a crear.
     * @param hashedPassword Contraseña encriptada.
     * @return Account con los datos de la nueva cuenta.
     */
    private Account construirCuenta(CrearCuentaDTO cuenta, String hashedPassword) {
        Account account = new Account();
        account.setEmail(cuenta.email());
        account.setPassword(hashedPassword);
        account.setRol(Rol.DOCTOR);
        account.setStatus(AccountStatus.INACTIVE);

        // Generar y asignar código de validación
        ValidationCode validationCode = new ValidationCode();
        validationCode.setCode(generateValidationCode());
        account.setRegistrationValidationCode(validationCode);

        return account;
    }

    /**
     * Construye y devuelve un nuevo usuario asociado a la cuenta.
     *
     * @param cuenta DTO con la información del usuario.
     * @param account La cuenta asociada al usuario.
     * @return User con los datos del nuevo usuario.
     */
    private User construirUsuario(CrearCuentaDTO cuenta, Account account) {
        User user = new User();
        user.setIdNumber(cuenta.idNumber());
        user.setName(cuenta.name());
        user.setLastName(cuenta.lastName());
        user.setPhoneNumber(cuenta.phoneNumber());
        user.setAddress(cuenta.address());
        user.setBirthDate(cuenta.fechaNacimiento());
        user.setAccount(account);
        return user;
    }

    /**
     * Genera un código de validación.
     *
     * @return String con el código de validación generado.
     */
    private String generateValidationCode() {
        return String.format("%05d", new SecureRandom().nextInt(100000));
    }

    /**
     * Obtiene el perfil del paciente basado en su identificación.
     *
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
     *
     * @param accountId           Número de identificación del usuario.
     * @param actualizarPerfilDTO DTO con los datos a actualizar.
     * @throws UserNotFoundException si el usuario no existe.
     */
    @Override
    @Transactional
    public void actualizarPerfil(Long accountId, ActualizarPerfilDTO actualizarPerfilDTO)
            throws UserNotFoundException, IllegalArgumentException {

        // 1️⃣ Validar que los parámetros no sean nulos
        if (accountId == null) {
            throw new IllegalArgumentException("El ID de la cuenta no puede ser nulo.");
        }

        if (actualizarPerfilDTO == null) {
            throw new IllegalArgumentException("Los datos de actualización no pueden ser nulos.");
        }

        // 2️⃣ Buscar la cuenta por su ID
        Optional<Account> accountOptional = accountRepository.findById(accountId);

        if (accountOptional.isEmpty()) {
            throw new UserNotFoundException("La cuenta con ID " + accountId + " no existe.");
        }

        // 3️⃣ Obtener la cuenta y verificar si tiene un usuario asociado
        Account account = accountOptional.get();
        User user = account.getUser();

        if (user == null) {
            throw new UserNotFoundException("No se encontró un usuario asociado a la cuenta con ID " + accountId);
        }

        // 4️⃣ Validar que los datos no sean nulos o vacíos
        if (actualizarPerfilDTO.name() == null || actualizarPerfilDTO.name().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }

        if (actualizarPerfilDTO.lastName() == null || actualizarPerfilDTO.lastName().trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido no puede estar vacío.");
        }

        if (actualizarPerfilDTO.phoneNumber() == null || !actualizarPerfilDTO.phoneNumber().matches("\\d{10}")) {
            throw new IllegalArgumentException("El número de teléfono debe contener exactamente 10 dígitos.");
        }

        if (actualizarPerfilDTO.address() == null || actualizarPerfilDTO.address().trim().isEmpty()) {
            throw new IllegalArgumentException("La dirección no puede estar vacía.");
        }

        // 5️⃣ Verificar si el número de teléfono ya está en uso por otro usuario (opcional)
        Optional<User> existingUser = userRepository.findByPhoneNumber(actualizarPerfilDTO.phoneNumber());
        if (existingUser.isPresent() && !existingUser.get().getIdNumber().equals(user.getIdNumber())) {
            throw new IllegalArgumentException("El número de teléfono ya está registrado en otro usuario.");
        }

        // 6️⃣ Actualizar los datos del usuario
        user.setName(actualizarPerfilDTO.name());
        user.setLastName(actualizarPerfilDTO.lastName());
        user.setPhoneNumber(actualizarPerfilDTO.phoneNumber());
        user.setAddress(actualizarPerfilDTO.address());

        // 7️⃣ Guardar los cambios en el usuario
        userRepository.save(user);
    }

    /**
     * Desactiva la cuenta del usuario.
     *
     * @param accountId Número de identificación del usuario.
     * @throws AccountNotFoundException si la cuenta no existe.
     */
    @Override
    @Transactional
    public void eliminarCuenta(Long accountId) throws AccountNotFoundException {

        // Buscar la cuenta en la base de datos
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("No se encontró una cuenta con ID " + accountId));

        // Verificar si la cuenta ya está inactiva
        if (account.getStatus() == AccountStatus.INACTIVE) {
            throw new IllegalStateException("La cuenta con ID " + accountId + " ya está inactiva.");
        }

        // Cambiar el estado de la cuenta a INACTIVE en lugar de eliminarla
        account.setStatus(AccountStatus.INACTIVE);

        try {
            // Guardar cambios en la base de datos
            accountRepository.save(account);
        } catch (Exception e) {
            throw new RuntimeException("Ocurrió un error al intentar inactivar la cuenta.", e);
        }
    }

    /**
     * Activa la cuenta del usuario.
     *
     * @param activateAccountDTO DTO con la información para activar la cuenta.
     * @return String con un mensaje de confirmación.
     * @throws AccountAlreadyActiveException  si la cuenta ya está activa.
     * @throws ValidationCodeExpiredException si el código de validación ha expirado.
     * @throws AccountNotFoundException       si la cuenta no se encuentra.
     */
    @Override
    @Transactional
    public String activateAccount(ActivateAccountDTO activateAccountDTO)
            throws AccountAlreadyActiveException, ValidationCodeExpiredException, AccountNotFoundException {

        // Buscar la cuenta por el código de activación
        Account account = accountRepository.findByRegistrationValidationCode_Code(activateAccountDTO.code())
                .orElseThrow(() -> new AccountNotFoundException("No se encontró una cuenta con el código: " + activateAccountDTO.code()));

        // Verificar si la cuenta ya está activa
        if (account.getStatus() == AccountStatus.ACTIVE) {
            throw new AccountAlreadyActiveException("La cuenta ya está activada.");
        }

        // Obtener el código de validación asociado a la cuenta
        ValidationCode validationCode = Optional.ofNullable(account.getRegistrationValidationCode())
                .orElseThrow(() -> new ValidationCodeExpiredException("El código de validación no existe."));

        // Validar si el código ha expirado
        if (validationCode.isExpired()) {
            throw new ValidationCodeExpiredException("El código de validación ha expirado.");
        }

        try {
            // **Eliminar el código de activación de la base de datos**
            account.setRegistrationValidationCode(null); // Desvincular de la entidad Account
            validationCodeRepository.delete(validationCode); // Eliminar de la BD

            // Activar la cuenta
            account.setStatus(AccountStatus.ACTIVE);
            accountRepository.save(account); // Guardar cambios en la base de datos

            return "Cuenta activada exitosamente.";
        } catch (Exception e) {
            throw new RuntimeException("Error activando la cuenta.", e);
        }
    }

    /**
     * Envía un código de activación al correo electrónico del usuario.
     *
     * @param email Correo electrónico del usuario.
     * @return String con un mensaje de confirmación.
     * @throws EmailNotFoundException si el correo electrónico no se encuentra.
     * @throws Exception              si ocurre un error general.
     */
    @Override
    @Transactional
    public String sendActiveCode(String email) throws EmailNotFoundException, Exception {
        // Buscar la cuenta por email
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("No se encontró una cuenta asociada al email: " + email));

        // Verificar si la cuenta ya está activa
        if (account.getStatus() == AccountStatus.ACTIVE) {
            return "La cuenta ya está activada, no es necesario un código de validación.";
        }

        // Si ya tiene un código de activación previo, eliminarlo
        ValidationCode existingCode = account.getRegistrationValidationCode();
        if (existingCode != null) {
            validationCodeRepository.delete(existingCode);
        }

        // Generar código de activación
        ValidationCode validationCode = new ValidationCode();
        validationCode.setCode(generateValidationCode());

        // Guardar el nuevo código en la BD antes de asignarlo a la cuenta
        validationCodeRepository.save(validationCode);

        // Asignar el nuevo código a la cuenta
        account.setRegistrationValidationCode(validationCode);
        accountRepository.save(account); // Guardar los cambios en la cuenta

        // Enviar el código por correo
        emailService.sendCodevalidation(email, validationCode.getCode());

        return "Código de validación de cuenta enviado al correo: " + email;
    }


    /**
     * Envía un código de recuperación de contraseña al correo electrónico del usuario.
     *
     * @param email Correo electrónico del usuario.
     * @return String con un mensaje de confirmación.
     * @throws EmailNotFoundException si el correo electrónico no se encuentra.
     * @throws Exception              si ocurre un error general.
     */
    @Override
    @Transactional
    public String sendPasswordRecoveryCode(String email) throws EmailNotFoundException, Exception {
        // Buscar la cuenta por email
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("No se encontró una cuenta asociada al email: " + email));

        // Verificar si la cuenta está activa antes de enviar el código
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new EmailNotFoundException("No se puede recuperar la contraseña de una cuenta inactiva.");
        }

        // Verificar si ya existe un código de recuperación y si aún es válido
        RecoveryCode existingCode = account.getRecoveryCode();
        if (existingCode != null && !existingCode.isExpired()) {
            throw new IllegalStateException("Ya tienes un código de recuperación activo. Revisa tu correo.");
        }

        // Si hay un código previo (aunque esté expirado), eliminarlo
        if (existingCode != null) {
            recoveryCodeRepository.delete(existingCode);
        }

        // Generar nuevo código de recuperación
        RecoveryCode recoveryCode = new RecoveryCode(generateValidationCode());

        // Guardar el nuevo código en la base de datos
        recoveryCode = recoveryCodeRepository.save(recoveryCode);

        // Asignar el nuevo código a la cuenta
        account.setRecoveryCode(recoveryCode);
        accountRepository.save(account);

        // Enviar el código por correo
        emailService.sendRecoveryCode(account.getEmail(), recoveryCode.getCode());

        return "Código de recuperación de contraseña enviado al correo: " + account.getEmail();
    }

    /**
     * Cambia el código de la contraseña.
     *
     * @param changePasswordDTO DTO con la información para cambiar el código de la contraseña.
     * @return String con un mensaje de confirmación.
     * @throws InvalidValidationCodeException si el código de validación es inválido.
     * @throws ValidationCodeExpiredException si el código de validación ha expirado.
     * @throws PasswordsDoNotMatchException   si las contraseñas no coinciden.
     * @throws Exception                      si ocurre un error general.
     */
    @Override
    @Transactional
    public String changePasswordCode(ChangePasswordCodeDTO changePasswordDTO)
            throws InvalidValidationCodeException, ValidationCodeExpiredException, PasswordsDoNotMatchException {

        // Buscar la cuenta por código de recuperación
        Account account = accountRepository.findByRecoveryCode_Code(changePasswordDTO.code())
                .orElseThrow(() -> new InvalidValidationCodeException("El código de recuperación no es válido."));

        // Obtener el código de recuperación
        RecoveryCode recoveryCode = account.getRecoveryCode();

        // Verificar si el código de recuperación es nulo o ha expirado
        if (recoveryCode == null || recoveryCode.isExpired()) {
            throw new ValidationCodeExpiredException("El código de recuperación ha expirado o no es válido.");
        }

        // Verificar si la cuenta está activa antes de cambiar la contraseña
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new InvalidValidationCodeException("No se puede cambiar la contraseña de una cuenta inactiva.");
        }

        // Verificar que las contraseñas coincidan
        if (!changePasswordDTO.newPassword().equals(changePasswordDTO.confirmationPassword())) {
            throw new PasswordsDoNotMatchException("Las contraseñas no coinciden.");
        }

        // Verificar que la nueva contraseña cumpla con requisitos mínimos de seguridad
        if (changePasswordDTO.newPassword().length() < 8) {
            throw new PasswordsDoNotMatchException("La nueva contraseña debe tener al menos 8 caracteres.");
        }

        // Eliminar el código de recuperación antes de cambiar la contraseña
        recoveryCodeRepository.delete(recoveryCode);
        account.setRecoveryCode(null);

        // Encriptar la nueva contraseña y actualizar la cuenta
        account.setPassword(passwordEncoder.encode(changePasswordDTO.newPassword()));
        accountRepository.save(account);

        return "La contraseña ha sido cambiada exitosamente.";
    }

    /**
     * Actualiza la contraseña del usuario.
     *
     * @param id                Número de identificación del usuario.
     * @param updatePasswordDTO DTO con la nueva contraseña.
     * @return String con un mensaje de confirmación.
     * @throws AccountNotFoundException        si la cuenta no se encuentra.
     * @throws InvalidCurrentPasswordException si la contraseña actual es incorrecta.
     * @throws PasswordMismatchException       si las contraseñas no coinciden.
     */
    @Override
    @Transactional
    public String updatePassword(Long id, UpdatePasswordDTO updatePasswordDTO)
            throws AccountNotFoundException, InvalidCurrentPasswordException,
            PasswordMismatchException {

        // Buscar la cuenta en la base de datos utilizando el ID proporcionado
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("No se encontró la cuenta con ID: " + id));

        // Verificar si la cuenta está activa antes de actualizar la contraseña
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountNotFoundException("No se puede actualizar la contraseña de una cuenta inactiva.");
        }

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

        // Evitar que el usuario use la misma contraseña actual
        if (passwordEncoder.matches(newPassword, account.getPassword())) {
            throw new PasswordMismatchException("La nueva contraseña no puede ser igual a la actual.");
        }

        // Encriptar y actualizar la nueva contraseña
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);

        return "La contraseña ha sido cambiada exitosamente.";
    }




}
