package edu.uniquindio.dentalmanagementsystembackend.repository;

import edu.uniquindio.dentalmanagementsystembackend.entity.Especialidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EspecialidadRepository extends JpaRepository<Especialidad, Long> {
    
    /**
     * Verifica si existe una especialidad con el ID especificado que está asociada a un doctor con el ID de documento especificado.
     * 
     * @param idNumber ID de documento del doctor
     * @param especialidadId ID de la especialidad
     * @return true si existe la relación, false en caso contrario
     */
    @Query("SELECT COUNT(e) > 0 FROM Especialidad e JOIN e.doctores d WHERE d.idNumber = :idNumber AND e.id = :especialidadId")
    boolean existsByDoctoresIdNumberAndId(@Param("idNumber") String idNumber, @Param("especialidadId") Long especialidadId);
} 