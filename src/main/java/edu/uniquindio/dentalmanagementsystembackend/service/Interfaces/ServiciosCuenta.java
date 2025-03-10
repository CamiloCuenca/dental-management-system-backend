package edu.uniquindio.dentalmanagementsystembackend.service.Interfaces;

import edu.uniquindio.dentalmanagementsystembackend.dto.JWT.TokenDTO;

import edu.uniquindio.dentalmanagementsystembackend.dto.account.*;
import edu.uniquindio.dentalmanagementsystembackend.exception.*;
import edu.uniquindio.dentalmanagementsystembackend.exception.InvalidIdFormatException;
import edu.uniquindio.dentalmanagementsystembackend.exception.ValidationCodeExpiredException;
import edu.uniquindio.dentalmanagementsystembackend.exception.InvalidCurrentPasswordException;
import edu.uniquindio.dentalmanagementsystembackend.exception.PasswordMismatchException;

public interface ServiciosCuenta {


    /**
     * Inicia sesión en el sistema.
     * @param loginDTO DTO con las credenciales de inicio de sesión.
     * @return TokenDTO con el token de autenticación.
     * @throws Exception si ocurre un error general.
     * @throws UserNotFoundException si el usuario no se encuentra.
     * @throws AccountInactiveException si la cuenta está inactiva.
     * @throws InvalidPasswordException si la contraseña es incorrecta.
     */
    TokenDTO login(LoginDTO loginDTO) throws Exception, UserNotFoundException, AccountInactiveException, InvalidPasswordException;

    /**
     * Crea una nueva cuenta de usuario.
     * @param cuenta DTO con la información de la cuenta a crear.
     * @return String con un mensaje de confirmación.
     * @throws Exception si ocurre un error general.
     * @throws EmailAlreadyExistsException si el correo electrónico ya está registrado.
     * @throws UserAlreadyExistsException si el usuario ya existe.
     */
    String crearCuenta(CrearCuentaDTO cuenta) throws Exception, EmailAlreadyExistsException, UserAlreadyExistsException;

    /**
     * Obtiene el perfil del paciente basado en su identificación.
     * @param id Número de identificación del paciente.
     * @return PerfilDTO con la información del usuario.
     * @throws Exception si el usuario no existe.
     * @throws UserNotFoundException si el usuario no se encuentra.
     * @throws InvalidIdFormatException si el formato del ID es inválido.
     */
    PerfilDTO obtenerPerfil(Long id) throws Exception, UserNotFoundException, InvalidIdFormatException;

    /**
     * Actualiza los datos personales del usuario.
     * @param id Número de identificación del usuario.
     * @param actualizarPerfilDTO DTO con los datos a actualizar.
     * @throws Exception si el usuario no existe o hay algún error en la actualización.
     * @throws UserNotFoundException si el usuario no se encuentra.
     * @throws InvalidIdFormatException si el formato del ID es inválido.
     */
    void actualizarPerfil(Long id, ActualizarPerfilDTO actualizarPerfilDTO) throws Exception, UserNotFoundException, InvalidIdFormatException;

    /**
     * Desactiva la cuenta del usuario.
     * @param id Número de identificación del usuario.
     * @throws Exception si el usuario no existe.
     * @throws UserNotFoundException si el usuario no se encuentra.
     * @throws InvalidIdFormatException si el formato del ID es inválido.
     */
    void eliminarCuenta(Long id) throws Exception, UserNotFoundException, InvalidIdFormatException;

    /**
     * Activa la cuenta del usuario.
     * @param activateAccountDTO DTO con la información para activar la cuenta.
     * @return String con un mensaje de confirmación.
     * @throws Exception si ocurre un error general.
     * @throws AccountAlreadyActiveException si la cuenta ya está activa.
     * @throws ValidationCodeExpiredException si el código de validación ha expirado.
     */
    String activateAccount(ActivateAccountDTO activateAccountDTO) throws Exception, AccountAlreadyActiveException, ValidationCodeExpiredException;

    /**
     * Envía un código de activación al correo electrónico del usuario.
     * @param email Correo electrónico del usuario.
     * @return String con un mensaje de confirmación.
     * @throws Exception si ocurre un error general.
     * @throws EmailNotFoundException si el correo electrónico no se encuentra.
     */
    String sendActiveCode(String email) throws Exception, EmailNotFoundException;

    /**
     * Cambia el código de la contraseña.
     * @param changePasswordCodeDTO DTO con la información para cambiar el código de la contraseña.
     * @return String con un mensaje de confirmación.
     * @throws Exception si ocurre un error general.
     * @throws InvalidValidationCodeException si el código de validación es inválido.
     * @throws ValidationCodeExpiredException si el código de validación ha expirado.
     * @throws PasswordsDoNotMatchException si las contraseñas no coinciden.
     */
    String changePasswordCode(ChangePasswordCodeDTO changePasswordCodeDTO) throws Exception, InvalidValidationCodeException, ValidationCodeExpiredException, PasswordsDoNotMatchException;

    /**
     * Actualiza la contraseña del usuario.
     * @param id Número de identificación del usuario.
     * @param updatePasswordDTO DTO con la nueva contraseña.
     * @return String con un mensaje de confirmación.
     * @throws Exception si ocurre un error general.
     * @throws InvalidCurrentPasswordException si la contraseña actual es incorrecta.
     * @throws PasswordMismatchException si las contraseñas no coinciden.
     */
    String updatePassword(Long id, UpdatePasswordDTO updatePasswordDTO) throws Exception, InvalidCurrentPasswordException, PasswordMismatchException;

    /**
     * Envía un código de recuperación de contraseña al correo electrónico del usuario.
     * @param correo Correo electrónico del usuario.
     * @return String con un mensaje de confirmación.
     * @throws Exception si ocurre un error general.
     * @throws EmailNotFoundException si el correo electrónico no se encuentra.
     */
    String sendPasswordRecoveryCode(String correo) throws Exception, EmailNotFoundException;



}
