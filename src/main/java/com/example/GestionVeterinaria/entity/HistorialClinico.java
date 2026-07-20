package com.example.GestionVeterinaria.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

// Entidad que representa la atención clínica registrada para una cita de una mascota
@Entity
@Table(name = "Historial_Clinico")
public class HistorialClinico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_Consulta")
    private LocalDate fechaConsulta;

    @Column(length = 200)
    private String diagnostico;

    @Column(length = 200)
    private String tratamiento;

    @Column(length = 200)
    private String observaciones;

    @ManyToOne
    @JoinColumn(name = "mascota_id", nullable = false)
    private Mascota mascota;

    @OneToOne
    @JoinColumn(name = "cita_id", nullable = false, unique = true)
    private Cita cita;

    public HistorialClinico() {}

    public HistorialClinico(Long id, LocalDate fechaConsulta, String diagnostico,
                            String tratamiento, String observaciones,
                            Mascota mascota, Cita cita) {
        this.id = id;
        this.fechaConsulta = fechaConsulta;
        this.diagnostico = diagnostico;
        this.tratamiento = tratamiento;
        this.observaciones = observaciones;
        this.mascota = mascota;
        this.cita = cita;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getFechaConsulta() { return fechaConsulta; }
    public void setFechaConsulta(LocalDate fechaConsulta) { this.fechaConsulta = fechaConsulta; }

    public String getDiagnostico() { return diagnostico; }
    public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }

    public String getTratamiento() { return tratamiento; }
    public void setTratamiento(String tratamiento) { this.tratamiento = tratamiento; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public Mascota getMascota() { return mascota; }
    public void setMascota(Mascota mascota) { this.mascota = mascota; }

    public Cita getCita() { return cita; }
    public void setCita(Cita cita) { this.cita = cita; }
}
