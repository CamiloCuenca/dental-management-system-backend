package edu.uniquindio.dentalmanagementsystembackend.entity.Account;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "telefono", length = 20)
    private String phoneNumber;

    @Column(name = "direccion", length = 255)
    private String address;

    // Relación bidireccional controlada desde Account
    @OneToOne(mappedBy = "user", cascade = CascadeType.MERGE)
    private Account account;


}
