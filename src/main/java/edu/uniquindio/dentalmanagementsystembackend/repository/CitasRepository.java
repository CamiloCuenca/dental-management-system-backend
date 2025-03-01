package edu.uniquindio.dentalmanagementsystembackend.repository;

import edu.uniquindio.dentalmanagementsystembackend.entity.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CitasRepository  extends JpaRepository <Cita,Long>{
}
