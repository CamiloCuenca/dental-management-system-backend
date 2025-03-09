package edu.uniquindio.dentalmanagementsystembackend.entity.Account;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@Entity
@Table(name = "codigos_recuperacion")
public class RecoveryCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "codigo", nullable = false, length = 10, unique = true)
    private String code;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime creationDate;

    public RecoveryCode() {
        this.creationDate = LocalDateTime.now();
    }

    public RecoveryCode(String code) {
        this.code = code;
        this.creationDate = LocalDateTime.now();
    }

    public boolean isExpired() {
        return creationDate.plusMinutes(15).isBefore(LocalDateTime.now());
    }
}
