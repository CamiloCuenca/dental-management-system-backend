package edu.uniquindio.dentalmanagementsystembackend.entity.Account;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Duration;
import java.time.LocalDateTime;


@Getter
@Setter
@Entity
@Table(name = "codigos_recuperacion")
public class RecoveryCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Cambiado de Integer a Long para mayor escalabilidad

    @Column(name = "codigo", nullable = false, length = 10, unique = true)
    private String code;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime creationDate; // Hibernate lo maneja automáticamente

    public RecoveryCode() {}

    public RecoveryCode(String code) {
        this.code = code;
    }

    public boolean isExpired() {
        return Duration.between(creationDate, LocalDateTime.now()).toMinutes() >= 15;
    }
}
