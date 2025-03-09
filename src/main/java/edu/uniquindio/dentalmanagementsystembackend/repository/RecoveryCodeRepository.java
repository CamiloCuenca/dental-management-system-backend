package edu.uniquindio.dentalmanagementsystembackend.repository;

import edu.uniquindio.dentalmanagementsystembackend.entity.Account.RecoveryCode;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.ValidationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecoveryCodeRepository extends JpaRepository<RecoveryCode,Long> {
}
