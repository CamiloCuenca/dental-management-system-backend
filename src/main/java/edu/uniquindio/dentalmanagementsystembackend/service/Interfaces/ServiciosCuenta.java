package edu.uniquindio.dentalmanagementsystembackend.service.Interfaces;

import edu.uniquindio.dentalmanagementsystembackend.dto.ActualizarPerfilDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.CrearCuentaDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.PerfilDTO;

public interface ServiciosCuenta {

    String crearCuenta(CrearCuentaDTO cuenta) throws Exception;

    /**
     * Obtiene el perfil del paciente basado en su identificación.
     * @param idNumber Número de identificación del paciente.
     * @return PerfilDTO con la información del usuario.
     * @throws Exception si el usuario no existe.
     */
    PerfilDTO obtenerPerfil(String idNumber) throws Exception;

    /**
     * Actualiza los datos personales del usuario.
     * @param idNumber Número de identificación del usuario.
     * @param actualizarPerfilDTO DTO con los datos a actualizar.
     * @throws Exception si el usuario no existe o hay algún error en la actualización.
     */
    void actualizarPerfil(String idNumber, ActualizarPerfilDTO actualizarPerfilDTO) throws Exception;


    /**
     * Desactiva la cuenta del usuario.
     * @param idNumber Número de identificación del usuario.
     * @throws Exception si el usuario no existe.
     */
    void eliminarCuenta(String idNumber) throws Exception;

}
