package org.sample.backendfia.controller;

import org.sample.backendfia.dto.CoordinadorDTO;
import org.sample.backendfia.service.IServiceCoordinador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coordinadores")
public class CoordinadorController {

    @Autowired
    private IServiceCoordinador serviceCoordinador;

    @GetMapping
    public List<CoordinadorDTO> getAllCoordinadores() {
        return serviceCoordinador.findAll();
    }

    @GetMapping("/{id}")
    public CoordinadorDTO getCoordinadorById(@PathVariable Long id) {
        return serviceCoordinador.findById(id);
    }

    @PostMapping
    public CoordinadorDTO createCoordinador(@RequestBody CoordinadorDTO coordinadorDTO) {
        return serviceCoordinador.save(coordinadorDTO);
    }

    @PutMapping("/{id}")
    public CoordinadorDTO updateCoordinador(@PathVariable Long id, @RequestBody CoordinadorDTO coordinadorDTO) {
        coordinadorDTO.setId(id);
        return serviceCoordinador.save(coordinadorDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoordinador(@PathVariable Long id) {
        serviceCoordinador.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
