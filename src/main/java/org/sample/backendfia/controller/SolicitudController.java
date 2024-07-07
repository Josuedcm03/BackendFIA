package org.sample.backendfia.controller;

import org.sample.backendfia.dto.SolicitudDTO;
import org.sample.backendfia.service.IServiceSolicitud;
import org.sample.backendfia.util.DiaSemanaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {

    @Autowired
    private IServiceSolicitud serviceSolicitud;

    @GetMapping
    public List<SolicitudDTO> getAllSolicitudes() {
        return serviceSolicitud.findAll().stream()
                .peek(solicitud -> {
                    // Convertir el día de la semana a español para mostrar
                    String diaSemanaEnEspanol = DiaSemanaUtil.getDiaSemanaEnEspanol(solicitud.getFechaCita().getDayOfWeek());
                    solicitud.setDiaSemana(diaSemanaEnEspanol);
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public SolicitudDTO getSolicitudById(@PathVariable Long id) {
        SolicitudDTO solicitud = serviceSolicitud.findById(id);
        // Convertir el día de la semana a español para mostrar
        String diaSemanaEnEspanol = DiaSemanaUtil.getDiaSemanaEnEspanol(solicitud.getFechaCita().getDayOfWeek());
        solicitud.setDiaSemana(diaSemanaEnEspanol);
        return solicitud;
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

    @PutMapping("/{id}/cambiar-estado")
    public SolicitudDTO cambiarEstado(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String nuevoEstado = request.get("nuevoEstado");
        return serviceSolicitud.cambiarEstado(id, nuevoEstado);
    }
}
