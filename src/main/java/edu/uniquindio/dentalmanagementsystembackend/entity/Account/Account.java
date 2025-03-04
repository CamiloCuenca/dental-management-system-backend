package edu.uniquindio.dentalmanagementsystembackend.entity.Account;

import edu.uniquindio.dentalmanagementsystembackend.Enum.AccountStatus;
import edu.uniquindio.dentalmanagementsystembackend.Enum.Rol;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "cuentas")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;

    @Column(name = "user_password", nullable = false)
    private String password;

    @CreationTimestamp
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime registrationDate;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "codigo_activacion_id", referencedColumnName = "id")
    private ValidationCode registrationValidationCode;

    @Column(name = "estado", nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @Column(name = "rol", nullable = false)
    @Enumerated(EnumType.STRING)
    private Rol rol;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id_number", unique = true)
    private User user;
}