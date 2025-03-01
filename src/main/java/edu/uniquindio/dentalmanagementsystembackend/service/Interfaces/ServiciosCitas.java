package edu.uniquindio.dentalmanagementsystembackend.service.Interfaces;

import edu.uniquindio.dentalmanagementsystembackend.dto.CitaDTO;

public interface ServiciosCitas {

    // Método para crear (agendar) una cita odontológica
    void crearCita(CitaDTO citaDTO);
}
