package edu.uniquindio.dentalmanagementsystembackend.service.Interfaces;

import edu.uniquindio.dentalmanagementsystembackend.dto.CitaDTO;

import java.util.ArrayList;
import java.util.List;

public interface ServiciosCitas {

    // Método para crear (agendar) una cita odontológica
    void crearCita(CitaDTO citaDTO);

    // Método para obtener la lista de citas de un paciente
    List<CitaDTO> obtenerCitasPorPaciente(Long idPaciente);
}
