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
    @Column(name = "id_number", nullable = false, unique = true, length = 20)
    private String idNumber;

    @Column(name = "nombre", nullable = false, length = 100)
    private String name;

    @Column(name = "apellido", nullable = false, length = 100)
    private String lastName;

    @Column(name = "telefono", length = 20)
    private String phoneNumber;

    @Column(name = "direccion", length = 255)
    private String address;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate birthDate; // Ahora usa LocalDate

    // Relación bidireccional controlada desde Account
    @OneToOne(mappedBy = "user", cascade = CascadeType.MERGE)
    private Account account;


}
