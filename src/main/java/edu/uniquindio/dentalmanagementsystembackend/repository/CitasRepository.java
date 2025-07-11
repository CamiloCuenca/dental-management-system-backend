package edu.uniquindio.dentalmanagementsystembackend.repository;

import edu.uniquindio.dentalmanagementsystembackend.entity.Account.User;
import edu.uniquindio.dentalmanagementsystembackend.entity.Cita;
import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoCitas;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
public interface CitasRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByPaciente_IdNumber(String idPaciente);
    Page<Cita> findByPaciente_IdNumber(String idPaciente, Pageable pageable);
    
    List<Cita> findByDoctor_IdNumber(String idDoctor);
    Page<Cita> findByDoctor_IdNumber(String idDoctor, Pageable pageable);
    
    List<Cita> findByFechaHoraBetween(Instant fechaInicio, Instant fechaFin);
    
    boolean existsByDoctorAndFechaHoraBetween(User doctor, Instant fechaInicio, Instant fechaFin);

    boolean existsByDoctorAndFechaHora(User doctor, Instant fechaHora);

   // 1. Buscar citas NO autenticadas por número de identificación (paciente no autenticado)
    @Query("SELECT c FROM Cita c WHERE c.numeroIdentificacionNoAutenticado = :numeroIdentificacion AND c.esAutenticada = false")
    List<Cita> findByNumeroIdentificacionNoAutenticadoAndEsAutenticadaFalse(
            @Param("numeroIdentificacion") String numeroIdentificacion
    );

    // 2. Buscar citas autenticadas por ID del doctor
    @Query("SELECT c FROM Cita c WHERE c.doctor.idNumber = :idDoctor AND c.esAutenticada = true")
    List<Cita> findByDoctor_IdNumberAndEsAutenticadaTrue(
            @Param("idDoctor") String idDoctor
    );
    
    // 2.1. Buscar citas autenticadas por ID del doctor con paginación
    @Query("SELECT c FROM Cita c WHERE c.doctor.idNumber = :idDoctor AND c.esAutenticada = true")
    Page<Cita> findByDoctor_IdNumberAndEsAutenticadaTrue(
            @Param("idDoctor") String idDoctor,
            Pageable pageable
    );

    // 3. Buscar citas NO autenticadas por ID del doctor
    @Query("SELECT c FROM Cita c WHERE c.doctor.idNumber = :idDoctor AND c.esAutenticada = false")
    List<Cita> findByDoctor_IdNumberAndEsAutenticadaFalse(
            @Param("idDoctor") String idDoctor
    );
    // Método nuevo más específico
    @Query("SELECT c FROM Cita c WHERE c.doctor.idNumber = :idDoctor AND c.esAutenticada = false " +
            "AND c.nombrePacienteNoAutenticado IS NOT NULL " +
            "AND c.numeroIdentificacionNoAutenticado IS NOT NULL")
    List<Cita> findValidNoAuthCitasByDoctorId(@Param("idDoctor") String idDoctor);




}
