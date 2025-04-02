package edu.uniquindio.dentalmanagementsystembackend.entity.Account;

import edu.uniquindio.dentalmanagementsystembackend.Enum.AccountStatus;
import edu.uniquindio.dentalmanagementsystembackend.Enum.Rol;
import edu.uniquindio.dentalmanagementsystembackend.Enum.TipoDoctor;
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

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id", referencedColumnName = "id_number", unique = true)
    private User user;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "validation_code_id")
    private ValidationCode registrationValidationCode;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "recovery_code_id") // Corregido: Antes estaba `Recovery_Code_id`
    private RecoveryCode recoveryCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true) // Puede ser null si no es doctor
    private TipoDoctor tipoDoctor;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // Fecha de creación automática

    // Método para asignar el tipo de doctor solo si el rol es DOCTOR
    public void setTipoDoctor(TipoDoctor tipoDoctor) {
        if (this.rol == Rol.DOCTOR) {
            this.tipoDoctor = tipoDoctor;
        } else {
            this.tipoDoctor = null;
        }
    }
}