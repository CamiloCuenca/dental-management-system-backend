package edu.uniquindio.dentalmanagementsystembackend.repository;

import edu.uniquindio.dentalmanagementsystembackend.Enum.Rol;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    boolean existsByIdNumber(String s);
    @Query("SELECT u FROM User u WHERE u.account.rol = :rol")
    List<User> findByRol(@Param("rol") Rol rol);

}
