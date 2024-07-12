package org.sample.backendfia.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class UpdateFechaHoraDTO {
    private LocalDate fecha;
    private LocalTime hora;

    // Getters y Setters
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
}
