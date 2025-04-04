package edu.uniquindio.dentalmanagementsystembackend.repository;

import edu.uniquindio.dentalmanagementsystembackend.entity.DisponibilidadDoctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DisponibilidadDoctorRepository extends JpaRepository<DisponibilidadDoctor, Long> {
    // Métodos personalizados si son necesarios
} 