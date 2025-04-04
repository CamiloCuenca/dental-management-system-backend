package edu.uniquindio.dentalmanagementsystembackend.service.Interfaces;


import edu.uniquindio.dentalmanagementsystembackend.dto.cita.CrearCitaDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.EditarCitaAdminDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.EditarCitaPacienteDTO;
import edu.uniquindio.dentalmanagementsystembackend.entity.Cita;

public interface ServiciosCitas {

    /**
     * Crea un nuevo registro en el historial médico.
     * @param dto DTO con la información del historial médico
     * @return HistorialMedico creado
     */
    Cita crearCita(CrearCitaDTO dto);

    /**
     * Elimina una cita existente.
     * @param citaId ID de la cita a eliminar
     */
    void eliminarCita(Long citaId);

    /**
     * Edita una cita existente como administrador.
     * @param dto DTO con la información completa de la cita a editar
     * @return Cita actualizada
     */
    Cita editarCitaAdmin(EditarCitaAdminDTO dto);

    /**
     * Edita una cita existente como paciente.
     * @param dto DTO con la información permitida para editar por el paciente
     * @param userId ID del usuario que realiza la edición
     * @return Cita actualizada
     */
    Cita editarCitaPaciente(EditarCitaPacienteDTO dto, Long userId);



    



}
