package edu.uniquindio.dentalmanagementsystembackend.entity.Account;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    @Column(name = "id_number", length = 20) // Asegura la longitud según la BD
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

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "usuario_id") // Se recomienda para claridad
    private Account account;

    @OneToMany(mappedBy = "paciente", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("fecha DESC")
    private List<HistorialMedico> historialesComoPaciente = new ArrayList<>();

    @OneToMany(mappedBy = "odontologo", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("fecha DESC")
    private List<HistorialMedico> historialesComoOdontologo = new ArrayList<>();

    // Métodos de utilidad
    public void agregarHistorialComoPaciente(HistorialMedico historial) {
        historialesComoPaciente.add(historial);
        historial.setPaciente(this);
    }

    public void agregarHistorialComoOdontologo(HistorialMedico historial) {
        historialesComoOdontologo.add(historial);
        historial.setOdontologo(this);
    }

    public List<HistorialMedico> obtenerHistorialesRecientesComoPaciente() {
        return historialesComoPaciente.stream()
                .sorted((h1, h2) -> h2.getFecha().compareTo(h1.getFecha()))
                .toList();
    }

    public List<HistorialMedico> obtenerHistorialesRecientesComoOdontologo() {
        return historialesComoOdontologo.stream()
                .sorted((h1, h2) -> h2.getFecha().compareTo(h1.getFecha()))
                .toList();
    }
}
