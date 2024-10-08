package org.sample.backendfia.controller;

import org.sample.backendfia.dto.SolicitudDTO;
import org.sample.backendfia.dto.UpdateFechaHoraDTO;
import org.sample.backendfia.service.IServiceSolicitud;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {

    @Autowired
    private IServiceSolicitud serviceSolicitud;

    @GetMapping
    public List<SolicitudDTO> getAllSolicitudes() {
        return serviceSolicitud.findAll();
    }

    @GetMapping("/{id}")
    public SolicitudDTO getSolicitudById(@PathVariable Long id) {
        return serviceSolicitud.findById(id);
    }

    @PostMapping
    public ResponseEntity<SolicitudDTO> createSolicitud(@RequestBody SolicitudDTO solicitudDTO) {
        if (solicitudDTO.getFecha().getDayOfWeek() == DayOfWeek.SATURDAY || solicitudDTO.getFecha().getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new IllegalArgumentException("No se pueden solicitar citas los fines de semana.");
        }
        SolicitudDTO createdSolicitud = serviceSolicitud.save(solicitudDTO);
        return new ResponseEntity<>(createdSolicitud, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public SolicitudDTO updateFechaCita(@PathVariable Long id, @RequestBody SolicitudDTO solicitudDTO) {
        if (solicitudDTO.getFecha().getDayOfWeek() == DayOfWeek.SATURDAY || solicitudDTO.getFecha().getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new IllegalArgumentException("No se pueden solicitar citas los fines de semana.");
        }
        return serviceSolicitud.updateFechaCita(id, solicitudDTO.getFecha(), solicitudDTO.getHora());
    }

    @PutMapping("/{id}/coordinador")
    public SolicitudDTO cambiarCoordinador(@PathVariable Long id, @RequestBody Long nuevoCoordinadorId) {
        return serviceSolicitud.cambiarCoordinador(id, nuevoCoordinadorId);
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelarCita(@PathVariable Long id) {
        serviceSolicitud.cancelarCita(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/estado")
    public SolicitudDTO cambiarEstado(@PathVariable Long id, @RequestBody String nuevoEstado) {
        return serviceSolicitud.cambiarEstado(id, nuevoEstado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSolicitud(@PathVariable Long id) {
        serviceSolicitud.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/estudiante/{estudianteId}")
    public List<SolicitudDTO> getSolicitudesByEstudianteId(@PathVariable Long estudianteId) {
        return serviceSolicitud.findByEstudianteId(estudianteId);
    }
    @PutMapping("/{id}/fecha")
    public ResponseEntity<SolicitudDTO> updateFechaCita(@PathVariable Long id, @RequestBody UpdateFechaHoraDTO request) {
        SolicitudDTO updatedSolicitud = serviceSolicitud.updateFechaCita(id, request.getFecha(), request.getHora());
        return ResponseEntity.ok(updatedSolicitud);
    }
}
