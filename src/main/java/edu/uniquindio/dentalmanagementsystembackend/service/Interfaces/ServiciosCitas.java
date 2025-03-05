package edu.uniquindio.dentalmanagementsystembackend.service.Interfaces;

import edu.uniquindio.dentalmanagementsystembackend.Enum.TipoCita;
import edu.uniquindio.dentalmanagementsystembackend.dto.CitaDTO;

import java.util.ArrayList;
import java.util.List;

public interface ServiciosCitas {


    /**
     * Método para crear (agendar) una cita odontológica
     * @param citaDTO
     */
    void crearCita(CitaDTO citaDTO);



    /**
     * Método para obtener la lista de citas de un paciente
     * @param idPaciente
     * @return
     */
    List<CitaDTO> obtenerCitasPorPaciente(Long idPaciente);

    /**
     *
     * @return
     */
    List<CitaDTO> obtenerTodasLasCitas(); // Obtener todas las citas

    /**
     *
     * @param idCita
     * @param nuevoTipoCita
     */
    void editarCita(Long idCita, TipoCita nuevoTipoCita); // Editar tipo de cita

    /**
     *
     * @param idCita
     */
    void cancelarCita(Long idCita); // Cancelar cita
}
