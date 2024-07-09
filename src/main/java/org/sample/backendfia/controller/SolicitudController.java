package org.sample.backendfia.controller;

import org.sample.backendfia.dto.SolicitudDTO;
import org.sample.backendfia.service.IServiceSolicitud;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        SolicitudDTO createdSolicitud = serviceSolicitud.save(solicitudDTO);
        return new ResponseEntity<>(createdSolicitud, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public SolicitudDTO updateFechaCita(@PathVariable Long id, @RequestBody SolicitudDTO solicitudDTO) {
        return serviceSolicitud.updateFechaCita(id, solicitudDTO.getFechaCita());
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
}
