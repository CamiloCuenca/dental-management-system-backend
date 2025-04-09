package edu.uniquindio.dentalmanagementsystembackend.entity.Account;

import edu.uniquindio.dentalmanagementsystembackend.Enum.Rol;
import edu.uniquindio.dentalmanagementsystembackend.entity.DisponibilidadDoctor;
import edu.uniquindio.dentalmanagementsystembackend.entity.Especialidad;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"account", "historialesComoPaciente", "historialesComoOdontologo", "disponibilidades", "especialidades"})
@EqualsAndHashCode(of = "idNumber", exclude = {"account", "historialesComoPaciente", "historialesComoOdontologo", "disponibilidades", "especialidades"})
@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "id_number", length = 20, nullable = false, unique = true)
    private String idNumber;

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
    private Account account;

    @OneToMany(mappedBy = "paciente", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("fecha DESC")
    private List<HistorialMedico> historialesComoPaciente = new ArrayList<>();

    @OneToMany(mappedBy = "doctor", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
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
        historial.setDoctor(this); // ← Corrección aplicada aquí
    }

    public List<HistorialMedico> obtenerHistorialesRecientesComoPaciente() {
        return historialesComoPaciente.stream()
                .sorted(Comparator.comparing(HistorialMedico::getFecha).reversed())
                .toList();
    }

    public List<HistorialMedico> obtenerHistorialesRecientesComoOdontologo() {
        return historialesComoOdontologo.stream()
                .sorted(Comparator.comparing(HistorialMedico::getFecha).reversed())
                .toList();
    }

    // Métodos de gestión de especialidades
    public void agregarEspecialidad(Especialidad especialidad) {
        if (esDoctor()) {
            this.especialidades.add(especialidad);
        } else {
            throw new IllegalStateException("Solo los usuarios con rol DOCTOR pueden tener especialidades");
        }
    }

    public void removerEspecialidad(Especialidad especialidad) {
        if (esDoctor()) {
            this.especialidades.remove(especialidad);
        } else {
            throw new IllegalStateException("Solo los usuarios con rol DOCTOR pueden gestionar especialidades");
        }
    }

    public boolean tieneEspecialidad(Especialidad especialidad) {
        return this.especialidades.contains(especialidad);
    }

    public Set<Especialidad> obtenerEspecialidadesSeguras() {
        if (esDoctor()) {
            return new HashSet<>(this.especialidades);
        }
        return Collections.emptySet();
    }

    // Método auxiliar para validación de rol
    private boolean esDoctor() {
        return this.account != null && this.account.getRol() == Rol.DOCTOR;
    }
}
