package com.example.GestionVeterinaria.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"veterinario_id", "fechaCita", "horaCita"}
        )
)
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "FechaCita")
    private LocalDate fechaCita;

    @Column(name = "horaCita")
    private LocalTime horaCita;

    @Column(length = 100)
    private String motivo;

    @ManyToOne
    @JoinColumn(name = "mascota_id", nullable = false)
    private Mascota mascota;

    @Column(length = 100)
    private String estado;

    @ManyToOne
    @JoinColumn(name = "veterinario_id", nullable = false)
    private Veterinario veterinario;

    @OneToOne(mappedBy = "cita", cascade = CascadeType.ALL, orphanRemoval = true)
    private HistorialClinico historialClinico;

    public Cita() {}

    public Cita(Long id, LocalDate fechaCita, LocalTime horaCita, String motivo,
                Mascota mascota, String estado, Veterinario veterinario,
                HistorialClinico historialClinico) {
        this.id = id;
        this.fechaCita = fechaCita;
        this.horaCita = horaCita;
        this.motivo = motivo;
        this.mascota = mascota;
        this.estado = estado;
        this.veterinario = veterinario;
        this.historialClinico = historialClinico;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getFechaCita() { return fechaCita; }
    public void setFechaCita(LocalDate fechaCita) { this.fechaCita = fechaCita; }

    public LocalTime getHoraCita() { return horaCita; }
    public void setHoraCita(LocalTime horaCita) { this.horaCita = horaCita; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public Mascota getMascota() { return mascota; }
    public void setMascota(Mascota mascota) { this.mascota = mascota; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Veterinario getVeterinario() { return veterinario; }
    public void setVeterinario(Veterinario veterinario) { this.veterinario = veterinario; }

    public HistorialClinico getHistorialClinico() { return historialClinico; }
    public void setHistorialClinico(HistorialClinico historialClinico) { this.historialClinico = historialClinico; }
}
