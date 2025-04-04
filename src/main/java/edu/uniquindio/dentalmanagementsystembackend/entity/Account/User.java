package edu.uniquindio.dentalmanagementsystembackend.entity.Account;

import edu.uniquindio.dentalmanagementsystembackend.Enum.Rol;
import edu.uniquindio.dentalmanagementsystembackend.entity.DisponibilidadDoctor;
import edu.uniquindio.dentalmanagementsystembackend.entity.Especialidad;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(exclude = "account")
@Entity
@Table(name = "users")
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

    @ManyToMany
    @JoinTable(
        name = "doctor_especialidad",
        joinColumns = @JoinColumn(name = "doctor_id"),
        inverseJoinColumns = @JoinColumn(name = "especialidad_id")
    )
    private Set<Especialidad> especialidades = new HashSet<>();
    
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DisponibilidadDoctor> disponibilidades = new ArrayList<>();

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

    // Métodos de gestión de especialidades
    public void agregarEspecialidad(Especialidad especialidad) {
        if (this.account != null && this.account.getRol() == Rol.DOCTOR) {
            this.especialidades.add(especialidad);
        } else {
            throw new IllegalStateException("Solo los usuarios con rol DOCTOR pueden tener especialidades");
        }
    }

    public void removerEspecialidad(Especialidad especialidad) {
        if (this.account != null && this.account.getRol() == Rol.DOCTOR) {
            this.especialidades.remove(especialidad);
        } else {
            throw new IllegalStateException("Solo los usuarios con rol DOCTOR pueden gestionar especialidades");
        }
    }

    public boolean tieneEspecialidad(Especialidad especialidad) {
        return this.especialidades.contains(especialidad);
    }

    public Set<Especialidad> getEspecialidades() {
        if (this.account != null && this.account.getRol() == Rol.DOCTOR) {
            return new HashSet<>(this.especialidades);
        }
        return new HashSet<>(); // Retorna un conjunto vacío si no es doctor
    }
}
