package edu.uniquindio.dentalmanagementsystembackend.service.Interfaces;

import edu.uniquindio.dentalmanagementsystembackend.dto.JWT.TokenDTO;

import edu.uniquindio.dentalmanagementsystembackend.dto.account.*;
import edu.uniquindio.dentalmanagementsystembackend.exception.*;
import edu.uniquindio.dentalmanagementsystembackend.exception.InvalidIdFormatException;
import edu.uniquindio.dentalmanagementsystembackend.exception.ValidationCodeExpiredException;
import edu.uniquindio.dentalmanagementsystembackend.exception.InvalidCurrentPasswordException;
import edu.uniquindio.dentalmanagementsystembackend.exception.PasswordMismatchException;

public interface ServiciosCuenta {

    TokenDTO login(LoginDTO loginDTO) throws Exception, UserNotFoundException, AccountInactiveException, InvalidPasswordException;


    String crearCuenta(CrearCuentaDTO cuenta) throws Exception, EmailAlreadyExistsException, UserAlreadyExistsException;

    /**
     * Obtiene el perfil del paciente basado en su identificación.
     * @param id Número de identificación del paciente.
     * @return PerfilDTO con la información del usuario.
     * @throws Exception si el usuario no existe.
     */
    PerfilDTO obtenerPerfil(Long id) throws Exception, UserNotFoundException, InvalidIdFormatException;

    /**
     * Actualiza los datos personales del usuario.
     * @param id Número de identificación del usuario.
     * @param actualizarPerfilDTO DTO con los datos a actualizar.
     * @throws Exception si el usuario no existe o hay algún error en la actualización.
     */
    void actualizarPerfil(Long id, ActualizarPerfilDTO actualizarPerfilDTO) throws Exception, UserNotFoundException, InvalidIdFormatException;


    /**
     * Desactiva la cuenta del usuario.
     * @param id Número de identificación del usuario.
     * @throws Exception si el usuario no existe.
     */
    void eliminarCuenta(Long id) throws Exception, UserNotFoundException, InvalidIdFormatException;


    String activateAccount(ActivateAccountDTO activateAccountDTO) throws Exception, AccountAlreadyActiveException, ValidationCodeExpiredException;

    String sendActiveCode(String email) throws Exception, EmailNotFoundException;

    String changePasswordCode(ChangePasswordDTO changePasswordDTO) throws Exception;

    String updatePassword(Long id ,UpdatePasswordDTO updatePasswordDTO) throws Exception, InvalidCurrentPasswordException, PasswordMismatchException;



}
