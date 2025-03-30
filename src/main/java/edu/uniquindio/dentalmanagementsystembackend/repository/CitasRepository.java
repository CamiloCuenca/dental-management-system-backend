package edu.uniquindio.dentalmanagementsystembackend.repository;

import edu.uniquindio.dentalmanagementsystembackend.entity.Account.User;
import edu.uniquindio.dentalmanagementsystembackend.entity.Cita;
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


}
