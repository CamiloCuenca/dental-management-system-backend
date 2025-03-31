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

/**
 * Implementación del servicio de gestión de cuentas de usuario.
 * Esta clase maneja toda la lógica de negocio relacionada con las cuentas,
 * incluyendo autenticación, registro, recuperación de contraseña y gestión de perfiles.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ServiciosCuentaImpl implements ServiciosCuenta {

    private final CuentaRepository accountRepository;
    private final UserRepository userRepository;
    private final validationCodeRepository validationCodeRepository;
    private final RecoveryCodeRepository recoveryCodeRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JWTUtils jwtUtils;

    /**
     * Construye los claims para el token JWT.
     *
     * @param account Cuenta del usuario
     * @return Mapa con los claims del token
     */
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
     * @param loginDTO DTO con las credenciales de inicio de sesión
     * @return TokenDTO con el token de autenticación
     * @throws UserNotFoundException si el usuario no existe
     * @throws AccountInactiveException si la cuenta está inactiva
     * @throws InvalidPasswordException si la contraseña es incorrecta
     */
    @Override
    @Transactional
    public TokenDTO login(LoginDTO loginDTO)
            throws UserNotFoundException, AccountInactiveException, InvalidPasswordException {
        validarLoginDTO(loginDTO);
        String idNumber = loginDTO.idNumber().trim();

        Account account = obtenerCuentaPorIdNumber(idNumber);
        validarEstadoCuenta(account);
        validarContraseña(account, loginDTO.password());

        Map<String, Object> claims = construirClaims(account);
        String token = jwtUtils.generateToken(account.getEmail(), claims);

        return new TokenDTO(token);
    }

    /**
     * Valida los datos del DTO de inicio de sesión.
     *
     * @param loginDTO DTO a validar
     * @throws IllegalArgumentException si los datos no son válidos
     */
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
     * @param cuenta DTO con la información de la cuenta
     * @return ID de la cuenta creada
     * @throws EmailAlreadyExistsException si el email ya existe
     * @throws UserAlreadyExistsException si el usuario ya existe
     * @throws DatabaseOperationException si hay error en la base de datos
     * @throws EmailSendingException si hay error al enviar el email
     */
    @Override
    @Transactional
    public String crearCuenta(CrearCuentaDTO cuenta)
            throws EmailAlreadyExistsException, UserAlreadyExistsException, DatabaseOperationException, EmailSendingException {
        validarCrearCuentaDTO(cuenta);
        validarExistenciaEmail(cuenta.email());
        validarExistenciaUsuario(cuenta.idNumber());

        try {
            String hashedPassword = passwordEncoder.encode(cuenta.password());
            Account newAccount = crearCuentaConDatos(cuenta, hashedPassword);
            User newUser = crearUsuarioConDatos(cuenta, newAccount);
            newAccount.setUser(newUser);

            Account createdAccount = accountRepository.save(newAccount);
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
     * @param cuenta DTO con la información de la cuenta
     * @throws IllegalArgumentException si los datos no son válidos
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
        if (cuenta.name() == null || cuenta.name().isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        if (cuenta.lastName() == null || cuenta.lastName().isBlank()) {
            throw new IllegalArgumentException("El apellido no puede estar vacío.");
        }
        if (cuenta.phoneNumber() == null || !cuenta.phoneNumber().matches("\\d{10}")) {
            throw new IllegalArgumentException("El número de teléfono debe contener exactamente 10 dígitos.");
        }
        if (cuenta.address() == null || cuenta.address().isBlank()) {
            throw new IllegalArgumentException("La dirección no puede estar vacía.");
        }
        if (cuenta.fechaNacimiento() == null) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede estar vacía.");
        }
    }

    /**
     * Valida que el email no exista en la base de datos.
     *
     * @param email Email a validar
     * @throws EmailAlreadyExistsException si el email ya existe
     */
    private void validarExistenciaEmail(String email) throws EmailAlreadyExistsException {
        if (accountRepository.findByEmail(email).isPresent()) {
            throw new EmailAlreadyExistsException("El email " + email + " ya está registrado.");
        }
    }

    /**
     * Valida que el usuario no exista en la base de datos.
     *
     * @param idNumber ID del usuario a validar
     * @throws UserAlreadyExistsException si el usuario ya existe
     */
    private void validarExistenciaUsuario(String idNumber) throws UserAlreadyExistsException {
        if (userRepository.existsByIdNumber(idNumber)) {
            throw new UserAlreadyExistsException("El usuario con ID " + idNumber + " ya existe.");
        }
    }

    /**
     * Crea una nueva cuenta con los datos proporcionados.
     *
     * @param cuenta DTO con los datos de la cuenta
     * @param hashedPassword Contraseña encriptada
     * @return Account creada
     */
    private Account crearCuentaConDatos(CrearCuentaDTO cuenta, String hashedPassword) {
        Account account = new Account();
        account.setEmail(cuenta.email());
        account.setPassword(hashedPassword);
        account.setRol(Rol.DOCTOR);
        account.setStatus(AccountStatus.INACTIVE);

        ValidationCode validationCode = new ValidationCode();
        validationCode.setCode(generateValidationCode());
        account.setRegistrationValidationCode(validationCode);

        return account;
    }

    /**
     * Crea un nuevo usuario con los datos proporcionados.
     *
     * @param cuenta DTO con los datos del usuario
     * @param account Cuenta asociada al usuario
     * @return User creado
     */
    private User crearUsuarioConDatos(CrearCuentaDTO cuenta, Account account) {
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
     * Elimina (desactiva) una cuenta.
     *
     * @param accountId ID de la cuenta
     * @throws AccountNotFoundException si la cuenta no existe
     */
    @Override
    @Transactional
    public void eliminarCuenta(Long accountId) throws AccountNotFoundException {
        Account account = obtenerCuentaPorId(accountId);
        validarEstadoCuentaParaEliminacion(account);
        desactivarCuenta(account);
    }

    /**
     * Valida que la cuenta pueda ser eliminada.
     *
     * @param account Cuenta a validar
     * @throws IllegalStateException si la cuenta ya está inactiva
     */
    private void validarEstadoCuentaParaEliminacion(Account account) {
        if (account.getStatus() == AccountStatus.INACTIVE) {
            throw new IllegalStateException("La cuenta con ID " + account.getId() + " ya está inactiva.");
        }
    }

    /**
     * Desactiva una cuenta.
     *
     * @param account Cuenta a desactivar
     */
    private void desactivarCuenta(Account account) {
        account.setStatus(AccountStatus.INACTIVE);
        accountRepository.save(account);
    }

    /**
     * Envía un código de recuperación de contraseña.
     *
     * @param email Email del usuario
     * @return Mensaje de confirmación
     * @throws EmailNotFoundException si el email no existe
     * @throws Exception si hay un error general
     */
    @Override
    @Transactional
    public String sendPasswordRecoveryCode(String email) throws EmailNotFoundException, Exception {
        Account account = obtenerCuentaPorEmail(email);
        validarEstadoCuentaParaRecuperacion(account);
        validarCodigoRecuperacionExistente(account);
        
        RecoveryCode recoveryCode = crearYGuardarCodigoRecuperacion(account);
        enviarCodigoRecuperacion(account.getEmail(), recoveryCode.getCode());

        return "Código de recuperación de contraseña enviado al correo: " + account.getEmail();
    }

    /**
     * Obtiene una cuenta por su email.
     *
     * @param email Email de la cuenta
     * @return Account encontrada
     * @throws EmailNotFoundException si no se encuentra la cuenta
     */
    private Account obtenerCuentaPorEmail(String email) throws EmailNotFoundException {
        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("No se encontró una cuenta asociada al email: " + email));
    }

    /**
     * Valida que la cuenta esté activa para recuperación.
     *
     * @param account Cuenta a validar
     * @throws EmailNotFoundException si la cuenta no está activa
     */
    private void validarEstadoCuentaParaRecuperacion(Account account) throws EmailNotFoundException {
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new EmailNotFoundException("No se puede recuperar la contraseña de una cuenta inactiva.");
        }
    }

    /**
     * Valida que no exista un código de recuperación activo.
     *
     * @param account Cuenta a validar
     * @throws IllegalStateException si ya existe un código activo
     */
    private void validarCodigoRecuperacionExistente(Account account) {
        RecoveryCode existingCode = account.getRecoveryCode();
        if (existingCode != null && !existingCode.isExpired()) {
            throw new IllegalStateException("Ya tienes un código de recuperación activo. Revisa tu correo.");
        }
        if (existingCode != null) {
            recoveryCodeRepository.delete(existingCode);
        }
    }

    /**
     * Crea y guarda un nuevo código de recuperación.
     *
     * @param account Cuenta para la que se crea el código
     * @return RecoveryCode creado
     */
    private RecoveryCode crearYGuardarCodigoRecuperacion(Account account) {
        RecoveryCode recoveryCode = new RecoveryCode(generateValidationCode());
        recoveryCode = recoveryCodeRepository.save(recoveryCode);
        account.setRecoveryCode(recoveryCode);
        accountRepository.save(account);
        return recoveryCode;
    }

    /**
     * Envía el código de recuperación por email.
     *
     * @param email Email del usuario
     * @param code Código de recuperación
     * @throws Exception si hay error al enviar el email
     */
    private void enviarCodigoRecuperacion(String email, String code) throws Exception {
        emailService.sendRecoveryCode(email, code);
    }

    /**
     * Genera un código de validación aleatorio.
     *
     * @return String con el código generado
     */
    private String generateValidationCode() {
        return String.format("%05d", new SecureRandom().nextInt(100000));
    }

    /**
     * Obtiene una cuenta por su ID.
     *
     * @param accountId ID de la cuenta
     * @return Account encontrada
     * @throws AccountNotFoundException si no se encuentra la cuenta
     */
    private Account obtenerCuentaPorId(Long accountId) throws AccountNotFoundException {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("No se encontró una cuenta con ID " + accountId));
    }

    /**
     * Obtiene una cuenta por su número de identificación.
     *
     * @param idNumber Número de identificación
     * @return Account encontrada
     * @throws UserNotFoundException si no se encuentra la cuenta
     */
    private Account obtenerCuentaPorIdNumber(String idNumber) throws UserNotFoundException {
        return accountRepository.findByIdUNumber(idNumber)
                .orElseThrow(() -> new UserNotFoundException("Usuario con ID " + idNumber + " no encontrado."));
    }

    /**
     * Valida que la cuenta esté activa.
     *
     * @param account Cuenta a validar
     * @throws AccountInactiveException si la cuenta no está activa
     */
    private void validarEstadoCuenta(Account account) throws AccountInactiveException {
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountInactiveException("La cuenta no está activa.");
        }
    }

    /**
     * Valida que la contraseña sea correcta.
     *
     * @param account Cuenta del usuario
     * @param password Contraseña a validar
     * @throws InvalidPasswordException si la contraseña es incorrecta
     */
    private void validarContraseña(Account account, String password) throws InvalidPasswordException {
        if (!passwordEncoder.matches(password, account.getPassword())) {
            throw new InvalidPasswordException("Contraseña incorrecta.");
        }
    }

    /**
     * Activa una cuenta usando el código de validación.
     *
     * @param activateAccountDTO DTO con el código de activación
     * @return Mensaje de confirmación
     * @throws AccountAlreadyActiveException si la cuenta ya está activa
     * @throws ValidationCodeExpiredException si el código ha expirado
     * @throws AccountNotFoundException si la cuenta no existe
     */
    @Override
    @Transactional
    public String activateAccount(ActivateAccountDTO activateAccountDTO)
            throws AccountAlreadyActiveException, ValidationCodeExpiredException, AccountNotFoundException {
        Account account = obtenerCuentaPorCodigoValidacion(activateAccountDTO.code());
        validarEstadoCuentaParaActivacion(account);
        validarCodigoActivacion(account);
        
        activarCuenta(account);
        return "Cuenta activada exitosamente.";
    }

    /**
     * Obtiene una cuenta por su código de validación.
     *
     * @param code Código de validación
     * @return Account encontrada
     * @throws AccountNotFoundException si no se encuentra la cuenta
     */
    private Account obtenerCuentaPorCodigoValidacion(String code) throws AccountNotFoundException {
        return accountRepository.findByRegistrationValidationCode_Code(code)
                .orElseThrow(() -> new AccountNotFoundException("No se encontró una cuenta con el código: " + code));
    }

    /**
     * Valida que la cuenta pueda ser activada.
     *
     * @param account Cuenta a validar
     * @throws AccountAlreadyActiveException si la cuenta ya está activa
     */
    private void validarEstadoCuentaParaActivacion(Account account) throws AccountAlreadyActiveException {
        if (account.getStatus() == AccountStatus.ACTIVE) {
            throw new AccountAlreadyActiveException("La cuenta ya está activada.");
        }
    }

    /**
     * Valida que el código de activación sea válido y no haya expirado.
     *
     * @param account Cuenta a validar
     * @throws ValidationCodeExpiredException si el código ha expirado
     */
    private void validarCodigoActivacion(Account account) throws ValidationCodeExpiredException {
        ValidationCode validationCode = Optional.ofNullable(account.getRegistrationValidationCode())
                .orElseThrow(() -> new ValidationCodeExpiredException("El código de validación no existe."));

        if (validationCode.isExpired()) {
            throw new ValidationCodeExpiredException("El código de validación ha expirado.");
        }
    }

    /**
     * Activa una cuenta y elimina su código de validación.
     *
     * @param account Cuenta a activar
     */
    private void activarCuenta(Account account) {
        ValidationCode validationCode = account.getRegistrationValidationCode();
        account.setRegistrationValidationCode(null);
        validationCodeRepository.delete(validationCode);
        account.setStatus(AccountStatus.ACTIVE);
        accountRepository.save(account);
    }

    /**
     * Envía un código de activación al correo electrónico del usuario.
     *
     * @param email Email del usuario
     * @return Mensaje de confirmación
     * @throws EmailNotFoundException si el email no existe
     * @throws Exception si hay un error general
     */
    @Override
    @Transactional
    public String sendActiveCode(String email) throws EmailNotFoundException, Exception {
        Account account = obtenerCuentaPorEmail(email);
        validarEstadoCuentaParaEnvioCodigo(account);
        validarCodigoActivacionExistente(account);
        
        ValidationCode validationCode = crearYGuardarCodigoActivacion(account);
        enviarCodigoActivacion(account.getEmail(), validationCode.getCode());

        return "Código de validación de cuenta enviado al correo: " + account.getEmail();
    }

    /**
     * Valida que la cuenta pueda recibir un código de activación.
     *
     * @param account Cuenta a validar
     * @throws EmailNotFoundException si la cuenta ya está activa
     */
    private void validarEstadoCuentaParaEnvioCodigo(Account account) throws EmailNotFoundException {
        if (account.getStatus() == AccountStatus.ACTIVE) {
            throw new EmailNotFoundException("La cuenta ya está activada, no es necesario un código de validación.");
        }
    }

    /**
     * Valida y elimina cualquier código de activación existente.
     *
     * @param account Cuenta a validar
     */
    private void validarCodigoActivacionExistente(Account account) {
        ValidationCode existingCode = account.getRegistrationValidationCode();
        if (existingCode != null) {
            validationCodeRepository.delete(existingCode);
        }
    }

    /**
     * Crea y guarda un nuevo código de activación.
     *
     * @param account Cuenta para la que se crea el código
     * @return ValidationCode creado
     */
    private ValidationCode crearYGuardarCodigoActivacion(Account account) {
        ValidationCode validationCode = new ValidationCode();
        validationCode.setCode(generateValidationCode());
        validationCode = validationCodeRepository.save(validationCode);
        account.setRegistrationValidationCode(validationCode);
        accountRepository.save(account);
        return validationCode;
    }

    /**
     * Envía el código de activación por email.
     *
     * @param email Email del usuario
     * @param code Código de activación
     * @throws Exception si hay error al enviar el email
     */
    private void enviarCodigoActivacion(String email, String code) throws Exception {
        emailService.sendCodevalidation(email, code);
    }

    /**
     * Cambia la contraseña usando un código de recuperación.
     *
     * @param changePasswordDTO DTO con la información para cambiar la contraseña
     * @return Mensaje de confirmación
     * @throws InvalidValidationCodeException si el código no es válido
     * @throws ValidationCodeExpiredException si el código ha expirado
     * @throws PasswordsDoNotMatchException si las contraseñas no coinciden
     */
    @Override
    @Transactional
    public String changePasswordCode(ChangePasswordCodeDTO changePasswordDTO)
            throws InvalidValidationCodeException, ValidationCodeExpiredException, PasswordsDoNotMatchException {
        Account account = obtenerCuentaPorCodigoRecuperacion(changePasswordDTO.code());
        validarEstadoCuentaParaCambioContraseña(account);
        validarCodigoRecuperacion(account);
        validarNuevaContraseña(changePasswordDTO);
        
        actualizarContraseña(account, changePasswordDTO.newPassword());
        return "La contraseña ha sido cambiada exitosamente.";
    }

    /**
     * Obtiene una cuenta por su código de recuperación.
     *
     * @param code Código de recuperación
     * @return Account encontrada
     * @throws InvalidValidationCodeException si el código no es válido
     */
    private Account obtenerCuentaPorCodigoRecuperacion(String code) throws InvalidValidationCodeException {
        return accountRepository.findByRecoveryCode_Code(code)
                .orElseThrow(() -> new InvalidValidationCodeException("El código de recuperación no es válido."));
    }

    /**
     * Valida que la cuenta pueda cambiar su contraseña.
     *
     * @param account Cuenta a validar
     * @throws InvalidValidationCodeException si la cuenta no está activa
     */
    private void validarEstadoCuentaParaCambioContraseña(Account account) throws InvalidValidationCodeException {
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new InvalidValidationCodeException("No se puede cambiar la contraseña de una cuenta inactiva.");
        }
    }

    /**
     * Valida que el código de recuperación sea válido y no haya expirado.
     *
     * @param account Cuenta a validar
     * @throws ValidationCodeExpiredException si el código ha expirado
     */
    private void validarCodigoRecuperacion(Account account) throws ValidationCodeExpiredException {
        RecoveryCode recoveryCode = account.getRecoveryCode();
        if (recoveryCode == null || recoveryCode.isExpired()) {
            throw new ValidationCodeExpiredException("El código de recuperación ha expirado o no es válido.");
        }
    }

    /**
     * Valida que la nueva contraseña cumpla con los requisitos.
     *
     * @param dto DTO con la información de la contraseña
     * @throws PasswordsDoNotMatchException si las contraseñas no coinciden o no cumplen requisitos
     */
    private void validarNuevaContraseña(ChangePasswordCodeDTO dto) throws PasswordsDoNotMatchException {
        if (!dto.newPassword().equals(dto.confirmationPassword())) {
            throw new PasswordsDoNotMatchException("Las contraseñas no coinciden.");
        }
        if (dto.newPassword().length() < 8) {
            throw new PasswordsDoNotMatchException("La nueva contraseña debe tener al menos 8 caracteres.");
        }
    }

    /**
     * Actualiza la contraseña de una cuenta.
     *
     * @param account Cuenta a actualizar
     * @param newPassword Nueva contraseña
     */
    private void actualizarContraseña(Account account, String newPassword) {
        RecoveryCode recoveryCode = account.getRecoveryCode();
        if (recoveryCode != null) {
            recoveryCodeRepository.delete(recoveryCode);
            account.setRecoveryCode(null);
        }
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
    }

    /**
     * Actualiza la contraseña de un usuario autenticado.
     *
     * @param id ID de la cuenta
     * @param updatePasswordDTO DTO con la información de la contraseña
     * @return Mensaje de confirmación
     * @throws AccountNotFoundException si la cuenta no existe
     * @throws InvalidCurrentPasswordException si la contraseña actual es incorrecta
     * @throws PasswordMismatchException si las contraseñas no coinciden
     */
    @Override
    @Transactional
    public String updatePassword(Long id, UpdatePasswordDTO updatePasswordDTO)
            throws AccountNotFoundException, InvalidCurrentPasswordException, PasswordMismatchException {
        Account account = obtenerCuentaPorId(id);
        validarCuentaActivaParaCambioContraseña(account);
        validarContraseñaActualCorrecta(account, updatePasswordDTO.currentPassword());
        validarNuevaContraseñaCumpleRequisitos(account, updatePasswordDTO);
        
        actualizarContraseñaUsuario(account, updatePasswordDTO.newPassword());
        return "La contraseña ha sido cambiada exitosamente.";
    }

    /**
     * Valida que la cuenta esté activa para permitir el cambio de contraseña.
     *
     * @param account Cuenta a validar
     * @throws AccountNotFoundException si la cuenta no está activa
     */
    private void validarCuentaActivaParaCambioContraseña(Account account) throws AccountNotFoundException {
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountNotFoundException("No se puede actualizar la contraseña de una cuenta inactiva.");
        }
    }

    /**
     * Valida que la contraseña actual ingresada sea correcta.
     *
     * @param account Cuenta del usuario
     * @param currentPassword Contraseña actual ingresada
     * @throws InvalidCurrentPasswordException si la contraseña es incorrecta
     */
    private void validarContraseñaActualCorrecta(Account account, String currentPassword) throws InvalidCurrentPasswordException {
        if (!passwordEncoder.matches(currentPassword, account.getPassword())) {
            throw new InvalidCurrentPasswordException("La contraseña actual es incorrecta.");
        }
    }

    /**
     * Valida que la nueva contraseña cumpla con todos los requisitos de seguridad.
     *
     * @param account Cuenta del usuario
     * @param dto DTO con la información de la contraseña
     * @throws PasswordMismatchException si las contraseñas no coinciden o no cumplen requisitos
     */
    private void validarNuevaContraseñaCumpleRequisitos(Account account, UpdatePasswordDTO dto) throws PasswordMismatchException {
        if (!dto.newPassword().equals(dto.confirmationPassword())) {
            throw new PasswordMismatchException("La nueva contraseña y la confirmación no coinciden.");
        }
        if (passwordEncoder.matches(dto.newPassword(), account.getPassword())) {
            throw new PasswordMismatchException("La nueva contraseña no puede ser igual a la actual.");
        }
    }

    /**
     * Actualiza la contraseña del usuario en la base de datos.
     *
     * @param account Cuenta a actualizar
     * @param newPassword Nueva contraseña a guardar
     */
    private void actualizarContraseñaUsuario(Account account, String newPassword) {
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
    }
}
