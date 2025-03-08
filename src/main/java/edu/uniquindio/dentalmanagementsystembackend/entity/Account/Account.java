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
    private Long id; // ID único de la cuenta

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id_number", unique = true)
    private User user;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "validation_code_id")
    private ValidationCode registrationValidationCode;
}