package edu.uniquindio.dentalmanagementsystembackend.repository;

import edu.uniquindio.dentalmanagementsystembackend.Enum.Rol;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.Account;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CuentaRepository extends JpaRepository<Account,Long> {

    /**
     * Busca una cuenta por el número de identificación del usuario.
     * @param idNumber Número de identificación del usuario.
     * @return Cuenta encontrada.
     */
    @Query("SELECT a FROM Account a JOIN FETCH a.user WHERE a.user.idNumber = :idNumber")
    Optional<Account> findByIdUNumber(@Param("idNumber") String idNumber);

    /**
     * Busca una cuenta por el email.
     * @param email Email del usuario.
     * @return Cuenta encontrada.
     */
    @Query("SELECT a FROM Account a WHERE a.email = :email")
    Optional<Account> findByEmail(@Param("email") String email);

    /**
     * Busca una cuenta por el código de validación de registro.
     * @param code Código de validación de registro.
     * @return Cuenta encontrada.
     */
    Optional<Account> findByRegistrationValidationCode_Code(String code);

    /**
     * Busca una cuenta por el código de recuperación.
     * @param code Código de recuperación.
     * @return Cuenta encontrada.
     */
    Optional<Account> findByRecoveryCode_Code(String code);


    // ... métodos existentes ...

    /**
     * Busca todas las cuentas por rol.
     * @param rol Rol de las cuentas a buscar.
     * @return Lista de cuentas encontradas.
     */
    @Query("SELECT a FROM Account a JOIN FETCH a.user WHERE a.rol = :rol")
    List<Account> findByRol(@Param("rol") Rol rol);


    @Query("SELECT u.email FROM Account u WHERE u.rol = 'ADMINISTRATOR'")
    List<String> obtenerCorreosAdministradores();
}
