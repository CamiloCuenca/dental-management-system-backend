package edu.uniquindio.dentalmanagementsystembackend.service.Interfaces;

import edu.uniquindio.dentalmanagementsystembackend.dto.CrearCuentaDTO;

public interface ServiciosCuenta {

    String crearCuenta(CrearCuentaDTO cuenta) throws Exception;

}
