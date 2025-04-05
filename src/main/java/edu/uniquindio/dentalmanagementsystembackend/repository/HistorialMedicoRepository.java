package edu.uniquindio.dentalmanagementsystembackend.repository;

import edu.uniquindio.dentalmanagementsystembackend.entity.Account.HistorialMedico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio para la gestión de historiales médicos.
 * Proporciona métodos para acceder y manipular los datos de historiales médicos en la base de datos.
 */
@Repository
public interface HistorialMedicoRepository extends JpaRepository<HistorialMedico, Long> {
    
    /**
     * Busca todos los historiales médicos de un paciente específico.
     * Los resultados se ordenan por fecha de forma descendente (más recientes primero).
     *
     * @param pacienteId ID del paciente
     * @return Lista de historiales médicos del paciente
     */
    @Query("SELECT h FROM HistorialMedico h WHERE h.paciente.idNumber = :pacienteId ORDER BY h.fecha DESC")
    List<HistorialMedico> findByPacienteIdNumber(@Param("pacienteId") String pacienteId);
    
    /**
     * Busca todos los historiales médicos creados en una fecha específica.
     * Los resultados se ordenan por fecha de forma descendente.
     *
     * @param fecha Fecha para filtrar los historiales
     * @return Lista de historiales médicos de la fecha especificada
     */
    @Query("SELECT h FROM HistorialMedico h WHERE h.fecha = :fecha ORDER BY h.fecha DESC")
    List<HistorialMedico> findByFecha(@Param("fecha") LocalDate fecha);
    
    /**
     * Busca todos los historiales médicos creados por un odontólogo específico.
     * Los resultados se ordenan por fecha de forma descendente.
     *
     * @param odontologoId ID del odontólogo
     * @return Lista de historiales médicos creados por el odontólogo
     */
    @Query("SELECT h FROM HistorialMedico h WHERE h.doctor.idNumber = :odontologoId ORDER BY h.fecha DESC")
    List<HistorialMedico> findByOdontologoIdNumber(@Param("odontologoId") String odontologoId);

    /**
     * Busca historiales médicos por rango de fechas
     */
    @Query("SELECT h FROM HistorialMedico h WHERE h.fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY h.fecha DESC")
    List<HistorialMedico> findByFechaBetween(
        @Param("fechaInicio") LocalDate fechaInicio,
        @Param("fechaFin") LocalDate fechaFin
    );
} 