package org.sample.backendfia.controller;

import org.sample.backendfia.dto.SecretariaDTO;
import org.sample.backendfia.dto.SolicitudDTO;
import org.sample.backendfia.service.IServiceSecretaria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/secretarias")
public class SecretariaController {

    @Autowired
    private IServiceSecretaria serviceSecretaria;

    @GetMapping
    public List<SecretariaDTO> getAllSecretarias() {
        return serviceSecretaria.findAll();
    }

    @GetMapping("/{id}")
    public SecretariaDTO getSecretariaById(@PathVariable Long id) {
        return serviceSecretaria.findById(id);
    }

    @PostMapping
    public SecretariaDTO createSecretaria(@RequestBody SecretariaDTO secretariaDTO) {
        return serviceSecretaria.save(secretariaDTO);
    }

    @PutMapping("/{id}")
    public SecretariaDTO updateSecretaria(@PathVariable Long id, @RequestBody SecretariaDTO secretariaDTO) {
        secretariaDTO.setId(id);
        return serviceSecretaria.save(secretariaDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSecretaria(@PathVariable Long id) {
        serviceSecretaria.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/cambiar-estado-solicitud")
    public SolicitudDTO cambiarEstadoSolicitud(@PathVariable Long id, @RequestParam Long solicitudId, @RequestParam String nuevoEstado) {
        return serviceSecretaria.cambiarEstadoSolicitud(solicitudId, nuevoEstado);
    }
}
