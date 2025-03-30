package edu.uniquindio.dentalmanagementsystembackend.service.Interfaces;

import edu.uniquindio.dentalmanagementsystembackend.Enum.TipoCita;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.ListaCitasDTO;

import java.util.List;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.CitaDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.DoctorDisponibilidadDTO;

public interface ServiciosCitas {

    /**
     * Método para crear (agendar) una cita odontológica.
     * @param citaDTO DTO con la información de la cita a crear.
     */
    void crearCita(CitaDTO citaDTO) throws Exception;

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

    /**
     * Método para obtener las fechas más cercanas disponibles de todos los doctores.
     * @return Lista de objetos con el ID del doctor y las fechas disponibles.
     */
    List<DoctorDisponibilidadDTO> obtenerFechasDisponiblesDoctores();


}

