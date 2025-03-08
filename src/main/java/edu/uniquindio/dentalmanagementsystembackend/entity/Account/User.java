package edu.uniquindio.dentalmanagementsystembackend.entity.Account;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(exclude = "account")
@Entity
@Table(name = "usuarios_detalles")
public class User {

    @Id
    @Column(name = "id_number")
    private String idNumber; // Cédula como clave primaria

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private LocalDate birthDate;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Account account;


}
