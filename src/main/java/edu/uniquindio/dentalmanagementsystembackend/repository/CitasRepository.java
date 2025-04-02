package edu.uniquindio.dentalmanagementsystembackend.repository;

import edu.uniquindio.dentalmanagementsystembackend.entity.Account.User;
import edu.uniquindio.dentalmanagementsystembackend.entity.Cita;
import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoCitas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CitasRepository  extends JpaRepository <Cita,Long>{

    /**
     * Busca las citas de un paciente por su número de identificación.
     * @param pacienteId Número de identificación del paciente.
     * @return Lista de citas del paciente.
     */
    @Query("SELECT c FROM Cita c WHERE c.paciente.idNumber = :pacienteId")
    List<Cita> findByPacienteId(@Param("pacienteId") String pacienteId);

    /**
     * Busca una cita por el paciente y la fecha y hora de la cita.
     * @param paciente Objeto del paciente.
     * @param fechaHora Fecha y hora de la cita.
     * @return Cita encontrada.
     */
    @Query("SELECT c FROM Cita c WHERE c.paciente = :paciente AND c.fechaHora = :fechaHora")
    Optional<Cita> findByPacienteAndFechaHora(@Param("paciente") User paciente, @Param("fechaHora") LocalDateTime fechaHora);

    /**
     * Busca las citas de un odontólogo por la fecha.
     * @param odontologo Objeto del odontólogo.
     * @param fecha Fecha de las citas.
     * @return Lista de citas del odontólogo en la fecha especificada.
     */
    @Query("SELECT c FROM Cita c WHERE c.odontologo = :odontologo AND FUNCTION('DATE', c.fechaHora) = :fecha")
    List<Cita> findByOdontologoAndFecha(@Param("odontologo") User odontologo, @Param("fecha") LocalDate fecha);


    @Query("SELECT c FROM Cita c WHERE c.paciente = :paciente AND c.fechaHora BETWEEN :inicio AND :fin")
    List<Cita> findByPacienteAndFechaHoraBetween(@Param("paciente") User paciente, @Param("inicio") Instant inicio, @Param("fin") Instant fin);

    @Query("SELECT c FROM Cita c WHERE c.odontologo = :odontologo AND c.fechaHora BETWEEN :inicio AND :fin")
    List<Cita> findByOdontologoAndFechaHoraBetween(@Param("odontologo") User odontologo, @Param("inicio") Instant inicio, @Param("fin") Instant fin);

    /**
     * Busca las citas por fecha específica.
     * @param inicio Fecha de inicio del día.
     * @param fin Fecha de fin del día.
     * @return Lista de citas para la fecha especificada.
     */
    @Query("SELECT c FROM Cita c WHERE c.fechaHora BETWEEN :inicio AND :fin")
    List<Cita> findByFechaHoraBetween(@Param("inicio") Instant inicio, @Param("fin") Instant fin);

    /**
     * Busca las citas por estado.
     * @param estado Estado de las citas a buscar.
     * @return Lista de citas con el estado especificado.
     */
    @Query("SELECT c FROM Cita c WHERE c.estado = :estado")
    List<Cita> findByEstado(@Param("estado") EstadoCitas estado);

    /**
     * Busca las citas de un odontólogo.
     * @param odontologo Objeto del odontólogo.
     * @return Lista de citas del odontólogo.
     */
    @Query("SELECT c FROM Cita c WHERE c.odontologo = :odontologo")
    List<Cita> findByOdontologo(@Param("odontologo") User odontologo);

    /**
     * Busca las citas próximas de un paciente.
     * @param paciente Objeto del paciente.
     * @param fechaActual Fecha actual.
     * @param diasFuturos Número de días a futuro.
     * @return Lista de citas próximas del paciente.
     */
    @Query("SELECT c FROM Cita c WHERE c.paciente = :paciente AND c.fechaHora >= :fechaActual AND c.fechaHora <= :fechaFutura")
    List<Cita> findCitasProximasPaciente(
        @Param("paciente") User paciente,
        @Param("fechaActual") Instant fechaActual,
        @Param("fechaFutura") Instant fechaFutura
    );

    /**
     * Busca las citas de emergencia pendientes.
     * @param fechaActual Fecha actual.
     * @return Lista de citas de emergencia pendientes.
     */
    @Query("SELECT c FROM Cita c WHERE c.tipoCita = 'EMERGENCIA' AND c.estado = 'PENDIENTE' AND c.fechaHora >= :fechaActual")
    List<Cita> findCitasEmergenciaPendientes(@Param("fechaActual") Instant fechaActual);
}
