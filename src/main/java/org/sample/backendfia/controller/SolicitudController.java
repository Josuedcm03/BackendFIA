package org.sample.backendfia.controller;

import org.sample.backendfia.dto.SolicitudDTO;
import org.sample.backendfia.service.IServiceSolicitud;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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

    @PutMapping("/{id}/cambiar-fecha")
    public SolicitudDTO updateFechaCita(@PathVariable Long id, @RequestBody LocalDateTime nuevaFecha) {
        return serviceSolicitud.updateFechaCita(id, nuevaFecha);
    }

    @PutMapping("/{id}/cambiar-coordinador")
    public SolicitudDTO cambiarCoordinador(@PathVariable Long id, @RequestBody Long nuevoCoordinadorId) {
        return serviceSolicitud.cambiarCoordinador(id, nuevoCoordinadorId);
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelarCita(@PathVariable Long id) {
        serviceSolicitud.cancelarCita(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/cambiar-estado")
    public SolicitudDTO cambiarEstado(@PathVariable Long id, @RequestBody Map<String, String> nuevoEstado) {
        return serviceSolicitud.cambiarEstado(id, nuevoEstado.get("nuevoEstado"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSolicitud(@PathVariable Long id) {
        serviceSolicitud.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
