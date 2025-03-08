package edu.uniquindio.dentalmanagementsystembackend.service.Interfaces;

import edu.uniquindio.dentalmanagementsystembackend.dto.cita.CitaDTO;

public interface ServiciosCitas {

    // Método para crear (agendar) una cita odontológica
    void crearCita(CitaDTO citaDTO);
}
