package org.sample.backendfia.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class
Solicitud {
    public static final String PENDIENTE = "Pendiente";
    public static final String APROBADA = "Aprobada";
    public static final String RECHAZADA = "Rechazada";
    public static final String CANCELADA = "Cancelada";
    public static final String REPROGRAMADA = "Reprogramada";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @ManyToOne
    @JoinColumn(name = "coordinador_id", nullable = false)
    private Coordinador coordinador;

    private String estado;

    private LocalDateTime fechaSolicitud;

    private LocalDateTime fechaCita;

    @Enumerated(EnumType.STRING)
    private Motivo motivo;

    private int duracionCita;

    public enum Motivo {
        PERSONAL, FINANCIERO, ACADEMICO
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
    }

    public Coordinador getCoordinador() {
        return coordinador;
    }

    public void setCoordinador(Coordinador coordinador) {
        this.coordinador = coordinador;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDateTime fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public LocalDateTime getFechaCita() {
        return fechaCita;
    }

    public void setFechaCita(LocalDateTime fechaCita) {
        this.fechaCita = fechaCita;
    }

    public Motivo getMotivo() {
        return motivo;
    }

    public void setMotivo(Motivo motivo) {
        this.motivo = motivo;
    }

    public int getDuracionCita() {
        return duracionCita;
    }

    public void setDuracionCita(int duracionCita) {
        this.duracionCita = duracionCita;
    }
}
