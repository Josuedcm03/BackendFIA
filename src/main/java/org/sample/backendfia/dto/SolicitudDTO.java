package org.sample.backendfia.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import org.sample.backendfia.model.Solicitud.Motivo;

public class SolicitudDTO {
    private Long id;
    private String estado;
    private LocalDate fechaSolicitud;
    private LocalDate fecha;
    private LocalTime hora;
    private Long estudianteId;
    private Long coordinadorId;
    private Long horarioId; // Nuevo campo para horario
    private Motivo motivo; // Utilizar la enumeración
    private int duracionCita;
    private String descripcionMotivo;
    private String diaSemana; // Nuevo campo para el día de la semana en español

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDate getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDate fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public Long getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(Long estudianteId) {
        this.estudianteId = estudianteId;
    }

    public Long getCoordinadorId() {
        return coordinadorId;
    }

    public void setCoordinadorId(Long coordinadorId) {
        this.coordinadorId = coordinadorId;
    }

    public Long getHorarioId() {
        return horarioId;
    }

    public void setHorarioId(Long horarioId) {
        this.horarioId = horarioId;
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

    public String getDescripcionMotivo() {
        return descripcionMotivo;
    }

    public void setDescripcionMotivo(String descripcionMotivo) {
        this.descripcionMotivo = descripcionMotivo;
    }

    public String getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
    }
}
