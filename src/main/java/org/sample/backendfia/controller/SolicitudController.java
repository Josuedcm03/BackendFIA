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
    public SolicitudDTO createSolicitud(@RequestBody SolicitudDTO solicitudDTO) {
        return serviceSolicitud.save(solicitudDTO);
    }

    @PutMapping("/{id}")
    public SolicitudDTO updateSolicitud(@PathVariable Long id, @RequestBody SolicitudDTO solicitudDTO) {
        solicitudDTO.setId(id);
        return serviceSolicitud.save(solicitudDTO);
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
