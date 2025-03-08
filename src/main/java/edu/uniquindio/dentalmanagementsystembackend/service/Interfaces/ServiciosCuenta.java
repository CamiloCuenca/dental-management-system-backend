package edu.uniquindio.dentalmanagementsystembackend.service.Interfaces;

import edu.uniquindio.dentalmanagementsystembackend.dto.JWT.TokenDTO;

import edu.uniquindio.dentalmanagementsystembackend.dto.account.ActualizarPerfilDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.account.CrearCuentaDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.account.LoginDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.account.PerfilDTO;
import edu.uniquindio.dentalmanagementsystembackend.exception.*;
import edu.uniquindio.dentalmanagementsystembackend.exception.InvalidIdFormatException;

public interface ServiciosCuenta {

    TokenDTO login(LoginDTO loginDTO) throws Exception, UserNotFoundException, AccountInactiveException, InvalidPasswordException;


    String crearCuenta(CrearCuentaDTO cuenta) throws Exception, EmailAlreadyExistsException, UserAlreadyExistsException;

    /**
     * Obtiene el perfil del paciente basado en su identificación.
     * @param idNumber Número de identificación del paciente.
     * @return PerfilDTO con la información del usuario.
     * @throws Exception si el usuario no existe.
     */
    PerfilDTO obtenerPerfil(String idNumber) throws Exception, UserNotFoundException, InvalidIdFormatException;

    /**
     * Actualiza los datos personales del usuario.
     * @param idNumber Número de identificación del usuario.
     * @param actualizarPerfilDTO DTO con los datos a actualizar.
     * @throws Exception si el usuario no existe o hay algún error en la actualización.
     */
    void actualizarPerfil(String idNumber, ActualizarPerfilDTO actualizarPerfilDTO) throws Exception, UserNotFoundException, InvalidIdFormatException;


    /**
     * Desactiva la cuenta del usuario.
     * @param idNumber Número de identificación del usuario.
     * @throws Exception si el usuario no existe.
     */
    void eliminarCuenta(String idNumber) throws Exception, UserNotFoundException, InvalidIdFormatException;

}
