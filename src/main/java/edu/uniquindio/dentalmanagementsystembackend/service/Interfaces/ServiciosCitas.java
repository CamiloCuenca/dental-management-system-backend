package edu.uniquindio.dentalmanagementsystembackend.service.Interfaces;

import edu.uniquindio.dentalmanagementsystembackend.Enum.TipoCita;
import edu.uniquindio.dentalmanagementsystembackend.dto.ListaCitasDTO;

import java.util.ArrayList;
import java.util.List;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.CitaDTO;

public interface ServiciosCitas {

    /**
     * Método para crear (agendar) una cita odontológica.
     * @param citaDTO DTO con la información de la cita a crear.
     */
    void crearCita(CitaDTO citaDTO);

    /**
     * Método para obtener la lista de citas de un paciente.
     * @param idPaciente Número de identificación del paciente.
     * @return Lista de citas del paciente.
     */
    List<ListaCitasDTO> obtenerCitasPorPaciente(Long idPaciente);

    /**
     * Método para obtener todas las citas.
     * @return Lista de todas las citas.
     */
    List<ListaCitasDTO> obtenerTodasLasCitas();

    /**
     * Método para editar el tipo de una cita.
     * @param idCita Número de identificación de la cita.
     * @param nuevoTipoCita Nuevo tipo de cita.
     */
    void editarCita(Long idCita, TipoCita nuevoTipoCita);

    /**
     * Método para cancelar una cita.
     * @param idCita Número de identificación de la cita.
     */
    void cancelarCita(Long idCita);
}
