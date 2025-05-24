package edu.uniquindio.dentalmanagementsystembackend.service.impl;

import edu.uniquindio.dentalmanagementsystembackend.Enum.AccountStatus;
import edu.uniquindio.dentalmanagementsystembackend.Enum.Rol;

import edu.uniquindio.dentalmanagementsystembackend.config.JWTUtils;
import edu.uniquindio.dentalmanagementsystembackend.dto.account.DoctorDTO;
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
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.AccountNotFoundException;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de gestión de cuentas de usuario.
 * Esta clase maneja toda la lógica de negocio relacionada con las cuentas,
 * incluyendo autenticación, registro, recuperación de contraseña y gestión de perfiles.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ServiciosCuentaImpl implements ServiciosCuenta {

    // ==============================================
    // DEPENDENCIAS
    // ==============================================
    private final CuentaRepository accountRepository;
    private final UserRepository userRepository;
    private final validationCodeRepository validationCodeRepository;
    private final RecoveryCodeRepository recoveryCodeRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JWTUtils jwtUtils;

    // ==============================================
    // MÉTODOS DE AUTENTICACIÓN Y TOKENS
    // ==============================================

    /**
     * Construye los claims para el token JWT.
     *
     * @param account Cuenta del usuario
     * @return Mapa con los claims del token
     */
    public Map<String, Object> construirClaims(Account account) {
        // Versión más robusta con validaciones
        if (account == null) {
            throw new IllegalArgumentException("Account no puede ser nulo");
        }

        Map<String, Object> claims = new LinkedHashMap<>(); // Mantiene orden

        claims.put("sub", account.getEmail()); // Subject estándar JWT
        claims.put("accountId", account.getId()); // ID principal
        claims.put("userId", account.getUser() != null ? account.getUser().getIdNumber() : null);
        claims.put("role", account.getRol()); // Mejor usar "role" que "rol" para estándares
        claims.put("email", account.getEmail());

        // Datos de usuario opcionales (con null checks)
        if (account.getUser() != null) {
            claims.put("given_name", account.getUser().getName()); // Estándar OpenID
            claims.put("family_name", account.getUser().getLastName());
        }

        claims.put("iat", System.currentTimeMillis() / 1000); // Fecha emisión
        claims.put("exp", (System.currentTimeMillis() / 1000) + 3600); // Expiración en 1h

        return Collections.unmodifiableMap(claims); // Map inmutable
    }

    /**
     * Inicia sesión en el sistema.
     * Valida las credenciales del usuario y genera un token JWT.
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
     * Genera un nuevo token JWT para una cuenta existente.
     */
    @Override
    public String generarNuevoToken(Long accountId) throws Exception, UserNotFoundException {
        Account account = obtenerCuentaPorId(accountId);
        if (account == null) {
            throw new AccountNotFoundException("No se encontró la cuenta con ID: " + accountId);
        }

        User user = account.getUser();
        if (user == null) {
            throw new UserNotFoundException("La cuenta no tiene un usuario asociado");
        }

        Map<String, Object> claims = construirClaims(account);
        return jwtUtils.generateToken(account.getEmail(), claims);
    }

   @Override
   public List<CuentaDTO> listarCuentasPaciente() throws Exception {
       return accountRepository.findByRol(Rol.PACIENTE)
               .stream()
               .filter(account -> account.getStatus() == AccountStatus.ACTIVE) // Filtrar solo cuentas activas
               .map(account -> new CuentaDTO(
                       account.getId(),
                       account.getUser().getIdNumber(),
                       account.getUser().getName(),
                       account.getUser().getLastName(),
                       account.getUser().getAddress(),
                       account.getUser().getBirthDate(),
                       account.getUser().getPhoneNumber(),
                       account.getEmail()
               ))
               .collect(Collectors.toList());
   }
    @Override
    public List<CuentaDTO> listarCuentasDoctor() throws Exception {
        return accountRepository.findByRol(Rol.DOCTOR)
                .stream()
                .filter(account -> account.getStatus() == AccountStatus.ACTIVE) // Filtrar solo cuentas activas
                .map(account -> new CuentaDTO(
                        account.getId(),
                        account.getUser().getIdNumber(),
                        account.getUser().getName(),
                        account.getUser().getLastName(),
                        account.getUser().getAddress(),
                        account.getUser().getBirthDate(),
                        account.getUser().getPhoneNumber(),
                        account.getEmail()
                ))
                .collect(Collectors.toList());
    }

    // ==============================================
    // MÉTODOS DE GESTIÓN DE CUENTAS
    // ==============================================

    /**
     * Crea una nueva cuenta de usuario.
     * Incluye validaciones y envío de código de activación.
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
     * Elimina (desactiva) una cuenta existente.
     */
    @Override
    @Transactional
    public void eliminarCuenta(Long accountId) throws AccountNotFoundException {
        Account account = obtenerCuentaPorId(accountId);
        validarEstadoCuentaParaEliminacion(account);
        desactivarCuenta(account);
    }

    /**
     * Obtiene el perfil completo de un usuario.
     */
    @Override
    public PerfilDTO obtenerPerfil(Long accountId) throws UserNotFoundException, AccountNotFoundException {
        log.info("Iniciando obtención de perfil para accountId: {}", accountId);

        Account account = obtenerCuentaPorId(accountId);
        log.debug("Cuenta encontrada: {}", account);

        User user = account.getUser();
        log.debug("Usuario asociado: {}", user);

        if (user == null) {
            log.error("No se encontró usuario asociado para accountId: {}", accountId);
            throw new UserNotFoundException("La cuenta con ID " + accountId + " no tiene un usuario asociado.");
        }

        PerfilDTO perfil = new PerfilDTO(
                user.getIdNumber(),
                user.getName(),
                user.getLastName(),
                user.getPhoneNumber(),
                user.getAddress(),
                user.getBirthDate(),
                account.getEmail()
        );

        log.info("Perfil obtenido exitosamente: {}", perfil);
        return perfil;
    }

    /**
     * Actualiza los datos de un usuario existente.
     */
    @Override
    @Transactional
    public String actualizarUsuario(Long accountId, ActualizarUsuarioDTO dto) throws UserNotFoundException, AccountNotFoundException {
        Account account = obtenerCuentaPorId(accountId);
        User user = account.getUser();

        if (user == null) {
            throw new UserNotFoundException("No se encontró un usuario asociado a la cuenta con ID " + accountId);
        }

        // Validaciones para evitar sobreescribir con valores nulos
        if (dto.name() != null) user.setName(dto.name());
        if (dto.lastName() != null) user.setLastName(dto.lastName());
        if (dto.phoneNumber() != null) user.setPhoneNumber(dto.phoneNumber());
        if (dto.address() != null) user.setAddress(dto.address());
        if (dto.email() != null) {
            account.setEmail(dto.email());
        }

        userRepository.save(user);
        accountRepository.save(account);  // Guardar account para actualizar el email

        return "Usuario actualizado exitosamente.";
    }

    // ==============================================
    // MÉTODOS DE ACTIVACIÓN DE CUENTAS
    // ==============================================

    /**
     * Activa una cuenta usando el código de validación.
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
     * Envía un nuevo código de activación al correo del usuario.
     */
    @Override
    @Transactional
    public String sendActiveCode(String email) throws EmailNotFoundException, AccountAlreadyActiveException, Exception {
        Account account = obtenerCuentaPorEmail(email);
        validarEstadoCuentaParaEnvioCodigo(account);
        validarCodigoActivacionExistente(account);

        ValidationCode validationCode = crearYGuardarCodigoActivacion(account);
        enviarCodigoActivacion(account.getEmail(), validationCode.getCode());

        return "Código de validación de cuenta enviado al correo: " + account.getEmail();
    }

    // ==============================================
    // MÉTODOS DE RECUPERACIÓN DE CONTRASEÑA
    // ==============================================

    /**
     * Envía un código de recuperación de contraseña.
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
     * Cambia la contraseña usando un código de recuperación.
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
     * Actualiza la contraseña de un usuario autenticado.
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

    // ==============================================
    // MÉTODOS PRIVADOS DE VALIDACIÓN
    // ==============================================

    /**
     * Valida los datos del DTO de inicio de sesión.
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
     * Valida los datos del DTO de creación de cuenta.
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
     */
    private void validarExistenciaEmail(String email) throws EmailAlreadyExistsException {
        if (accountRepository.findByEmail(email).isPresent()) {
            throw new EmailAlreadyExistsException("El email " + email + " ya está registrado.");
        }
    }

    /**
     * Valida que el usuario no exista en la base de datos.
     */
    private void validarExistenciaUsuario(String idNumber) throws UserAlreadyExistsException {
        if (userRepository.existsByIdNumber(idNumber)) {
            throw new UserAlreadyExistsException("El usuario con ID " + idNumber + " ya existe.");
        }
    }

    // ==============================================
    // MÉTODOS PRIVADOS DE CREACIÓN
    // ==============================================

    /**
     * Crea una nueva cuenta con los datos proporcionados.
     */
    private Account crearCuentaConDatos(CrearCuentaDTO cuenta, String hashedPassword) {
        Account account = new Account();
        account.setEmail(cuenta.email());
        account.setPassword(hashedPassword);
        account.setRol(Rol.PACIENTE);
        account.setStatus(AccountStatus.INACTIVE);

        ValidationCode validationCode = new ValidationCode();
        validationCode.setCode(generateValidationCode());
        account.setRegistrationValidationCode(validationCode);

        return account;
    }

    /**
     * Crea un nuevo usuario con los datos proporcionados.
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

    // ==============================================
    // MÉTODOS PRIVADOS DE GESTIÓN DE CÓDIGOS
    // ==============================================

    /**
     * Envía un código de validación al correo electrónico.
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
     * Genera un código de validación aleatorio.
     */
    private String generateValidationCode() {
        return String.format("%05d", new SecureRandom().nextInt(100000));
    }

    // ==============================================
    // MÉTODOS PRIVADOS DE CONSULTA
    // ==============================================

    /**
     * Obtiene una cuenta por su ID.
     */
    private Account obtenerCuentaPorId(Long accountId) throws AccountNotFoundException {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("No se encontró una cuenta con ID " + accountId));
    }

    /**
     * Obtiene una cuenta por su número de identificación.
     */
    private Account obtenerCuentaPorIdNumber(String idNumber) throws UserNotFoundException {
        return accountRepository.findByIdUNumber(idNumber)
                .orElseThrow(() -> new UserNotFoundException("Usuario con ID " + idNumber + " no encontrado."));
    }

    /**
     * Obtiene una cuenta por su email.
     */
    private Account obtenerCuentaPorEmail(String email) throws EmailNotFoundException {
        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("No se encontró una cuenta asociada al email: " + email));
    }

    /**
     * Obtiene una cuenta por su código de validación.
     */
    private Account obtenerCuentaPorCodigoValidacion(String code) throws AccountNotFoundException {
        return accountRepository.findByRegistrationValidationCode_Code(code)
                .orElseThrow(() -> new AccountNotFoundException("No se encontró una cuenta con el código: " + code));
    }

    /**
     * Obtiene una cuenta por su código de recuperación.
     */
    private Account obtenerCuentaPorCodigoRecuperacion(String code) throws InvalidValidationCodeException {
        return accountRepository.findByRecoveryCode_Code(code)
                .orElseThrow(() -> new InvalidValidationCodeException("El código de recuperación no es válido."));
    }

    // ==============================================
    // MÉTODOS PRIVADOS DE VALIDACIÓN DE ESTADO
    // ==============================================

    /**
     * Valida que la cuenta esté activa.
     */
    private void validarEstadoCuenta(Account account) throws AccountInactiveException {
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountInactiveException("La cuenta no está activa.");
        }
    }

    /**
     * Valida que la cuenta pueda ser eliminada.
     */
    private void validarEstadoCuentaParaEliminacion(Account account) {
        if (account.getStatus() == AccountStatus.INACTIVE) {
            throw new IllegalStateException("La cuenta con ID " + account.getId() + " ya está inactiva.");
        }
    }

    /**
     * Valida que la cuenta pueda ser activada.
     */
    private void validarEstadoCuentaParaActivacion(Account account) throws AccountAlreadyActiveException {
        if (account.getStatus() == AccountStatus.ACTIVE) {
            throw new AccountAlreadyActiveException("La cuenta ya está activada.");
        }
    }

    /**
     * Valida que la cuenta pueda recibir un código de activación.
     */
    private void validarEstadoCuentaParaEnvioCodigo(Account account) throws AccountAlreadyActiveException {
        if (account.getStatus() == AccountStatus.ACTIVE) {
            throw new AccountAlreadyActiveException("La cuenta ya está activada, no es necesario un código de validación.");
        }
    }

    /**
     * Valida que la cuenta pueda cambiar su contraseña.
     */
    private void validarEstadoCuentaParaCambioContraseña(Account account) throws InvalidValidationCodeException {
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new InvalidValidationCodeException("No se puede cambiar la contraseña de una cuenta inactiva.");
        }
    }

    // ==============================================
    // MÉTODOS PRIVADOS DE VALIDACIÓN DE CONTRASEÑA
    // ==============================================

    /**
     * Valida que la contraseña sea correcta.
     */
    private void validarContraseña(Account account, String password) throws InvalidPasswordException {
        if (!passwordEncoder.matches(password, account.getPassword())) {
            throw new InvalidPasswordException("Contraseña incorrecta.");
        }
    }

    /**
     * Valida que la contraseña actual sea correcta.
     */
    private void validarContraseñaActualCorrecta(Account account, String currentPassword) throws InvalidCurrentPasswordException {
        if (!passwordEncoder.matches(currentPassword, account.getPassword())) {
            throw new InvalidCurrentPasswordException("La contraseña actual es incorrecta.");
        }
    }

    /**
     * Valida que la nueva contraseña cumpla con los requisitos.
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
     * Valida que la nueva contraseña cumpla con todos los requisitos de seguridad.
     */
    private void validarNuevaContraseñaCumpleRequisitos(Account account, UpdatePasswordDTO dto) throws PasswordMismatchException {
        if (!dto.newPassword().equals(dto.confirmationPassword())) {
            throw new PasswordMismatchException("La nueva contraseña y la confirmación no coinciden.");
        }
        if (passwordEncoder.matches(dto.newPassword(), account.getPassword())) {
            throw new PasswordMismatchException("La nueva contraseña no puede ser igual a la actual.");
        }
    }

    // ==============================================
    // MÉTODOS PRIVADOS DE ACTUALIZACIÓN
    // ==============================================

    /**
     * Desactiva una cuenta.
     */
    private void desactivarCuenta(Account account) {
        account.setStatus(AccountStatus.INACTIVE);
        accountRepository.save(account);
    }

    /**
     * Activa una cuenta y elimina su código de validación.
     */
    private void activarCuenta(Account account) {
        ValidationCode validationCode = account.getRegistrationValidationCode();
        account.setRegistrationValidationCode(null);
        validationCodeRepository.delete(validationCode);
        account.setStatus(AccountStatus.ACTIVE);
        accountRepository.save(account);
    }

    /**
     * Actualiza la contraseña de una cuenta.
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
     * Actualiza la contraseña del usuario en la base de datos.
     */
    private void actualizarContraseñaUsuario(Account account, String newPassword) {
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
    }

    // ==============================================
    // MÉTODOS PRIVADOS DE GESTIÓN DE CÓDIGOS DE RECUPERACIÓN
    // ==============================================

    /**
     * Valida que no exista un código de recuperación activo.
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
     */
    private void enviarCodigoRecuperacion(String email, String code) throws Exception {
        emailService.sendRecoveryCode(email, code);
    }

    // ==============================================
    // MÉTODOS PRIVADOS DE GESTIÓN DE CÓDIGOS DE ACTIVACIÓN
    // ==============================================

    /**
     * Valida y elimina cualquier código de activación existente.
     */
    private void validarCodigoActivacionExistente(Account account) {
        ValidationCode existingCode = account.getRegistrationValidationCode();
        if (existingCode != null) {
            validationCodeRepository.delete(existingCode);
        }
    }

    /**
     * Crea y guarda un nuevo código de activación.
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
     */
    private void enviarCodigoActivacion(String email, String code) throws Exception {
        emailService.sendCodevalidation(email, code);
    }

    /**
     * Valida que el código de activación sea válido y no haya expirado.
     */
    private void validarCodigoActivacion(Account account) throws ValidationCodeExpiredException {
        ValidationCode validationCode = Optional.ofNullable(account.getRegistrationValidationCode())
                .orElseThrow(() -> new ValidationCodeExpiredException("El código de validación no existe."));

        if (validationCode.isExpired()) {
            throw new ValidationCodeExpiredException("El código de validación ha expirado.");
        }
    }

    /**
     * Valida que la cuenta esté activa para recuperación.
     */
    private void validarEstadoCuentaParaRecuperacion(Account account) throws EmailNotFoundException {
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new EmailNotFoundException("No se puede recuperar la contraseña de una cuenta inactiva.");
        }
    }

    /**
     * Valida que la cuenta esté activa para permitir el cambio de contraseña.
     */
    private void validarCuentaActivaParaCambioContraseña(Account account) throws AccountNotFoundException {
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountNotFoundException("No se puede actualizar la contraseña de una cuenta inactiva.");
        }
    }

    /**
     * Valida que el código de recuperación sea válido y no haya expirado.
     */
    private void validarCodigoRecuperacion(Account account) throws ValidationCodeExpiredException {
        RecoveryCode recoveryCode = account.getRecoveryCode();
        if (recoveryCode == null || recoveryCode.isExpired()) {
            throw new ValidationCodeExpiredException("El código de recuperación ha expirado o no es válido.");
        }
    }
}
