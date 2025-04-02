package edu.uniquindio.dentalmanagementsystembackend.service.Interfaces;

import edu.uniquindio.dentalmanagementsystembackend.Enum.TipoCita;
import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoCitas;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.ListaCitasDTO;

import java.util.List;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.CitaDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.DoctorDisponibilidadDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

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

    /**
     * Método para confirmar una cita.
     * @param idCita Número de identificación de la cita.
     */
    void confirmarCita(Long idCita);

    /**
     * Método para marcar una cita como completada.
     * @param idCita Número de identificación de la cita.
     */
    void completarCita(Long idCita);

    /**
     * Método para obtener citas por fecha específica.
     * @param fecha Fecha para filtrar las citas.
     * @return Lista de citas para la fecha especificada.
     */
    List<ListaCitasDTO> obtenerCitasPorFecha(LocalDate fecha);

    /**
     * Método para obtener citas por estado.
     * @param estado Estado de las citas a buscar.
     * @return Lista de citas con el estado especificado.
     */
    List<ListaCitasDTO> obtenerCitasPorEstado(EstadoCitas estado);


    /**
     * Método para reprogramar una cita.
     * @param idCita Número de identificación de la cita.
     * @param nuevaFechaHora Nueva fecha y hora para la cita.
     */
    void reprogramarCita(Long idCita, LocalDateTime nuevaFechaHora);

    /**
     * Método para obtener estadísticas de citas por estado.
     * @return Mapa con el conteo de citas por cada estado.
     */
    Map<EstadoCitas, Long> obtenerEstadisticasCitasPorEstado();

    /**
     * Método para obtener estadísticas de citas por doctor.
     * @return Mapa con el conteo de citas por cada doctor.
     */
    Map<Long, Long> obtenerEstadisticasCitasPorDoctor();

    /**
     * Método para enviar recordatorio de cita.
     * @param idCita Número de identificación de la cita.
     */
    void enviarRecordatorioCita(Long idCita);


}

