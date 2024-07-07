package org.sample.backendfia.controller;

import org.sample.backendfia.dto.CoordinadorDTO;
import org.sample.backendfia.service.IServiceCoordinador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coordinadores")
public class CoordinadorController {

    @Autowired
    private IServiceCoordinador serviceCoordinador;

    @PostMapping("/register")
    public CoordinadorDTO registerCoordinador(@RequestBody CoordinadorDTO coordinadorDTO) {
        return serviceCoordinador.save(coordinadorDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody CoordinadorDTO coordinadorDTO) {
        CoordinadorDTO coordinador = serviceCoordinador.findByEmail(coordinadorDTO.getEmail());
        if (coordinador != null && coordinadorDTO.getContrasena().equals(coordinador.getContrasena())) {
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @GetMapping
    public List<CoordinadorDTO> getAllCoordinadores() {
        return serviceCoordinador.findAll();
    }

    @GetMapping("/{id}")
    public CoordinadorDTO getCoordinadorById(@PathVariable Long id) {
        return serviceCoordinador.findById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoordinador(@PathVariable Long id) {
        serviceCoordinador.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
