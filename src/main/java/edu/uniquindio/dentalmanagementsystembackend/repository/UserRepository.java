package edu.uniquindio.dentalmanagementsystembackend.repository;

import edu.uniquindio.dentalmanagementsystembackend.Enum.Rol;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Checks if a user exists by their identification number.
     * @param s Identification number of the user.
     * @return true if the user exists, false otherwise.
     */
    boolean existsByIdNumber(String s);

    /**
     * Finds users by their role.
     * @param rol Role of the users to find.
     * @return List of users with the specified role.
     */
    @Query("SELECT u FROM User u WHERE u.account.rol = :rol")
    List<User> findByRol(@Param("rol") Rol rol);

    List<User> findByAccount_Rol(Rol rol);

    Optional<User> findByPhoneNumber(String phoneNumber);

    /**
     * Finds a user by their identification number.
     * @param idNumber Identification number of the user.
     * @return Optional containing the user if found.
     */
    Optional<User> findByIdNumber(String idNumber);



}
