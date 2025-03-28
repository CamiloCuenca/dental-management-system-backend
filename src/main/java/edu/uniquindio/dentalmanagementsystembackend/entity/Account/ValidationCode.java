package edu.uniquindio.dentalmanagementsystembackend.entity.Account;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "codigos_activacion")
public class ValidationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "codigo", nullable = false, length = 10, unique = true)
    private String code;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime creationDate;

    public ValidationCode() {
        this.creationDate = LocalDateTime.now();
    }

    public ValidationCode(String code) {
        this.code = code;
        this.creationDate = LocalDateTime.now();
    }

    public boolean isExpired() {
        return creationDate.plusMinutes(15).isBefore(LocalDateTime.now());
    }
}
