package org.sample.backendfia.controller;

import org.sample.backendfia.dto.EstudianteDTO;
import org.sample.backendfia.service.IServiceEstudiante;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estudiantes")
public class EstudianteController {

    @Autowired
    private IServiceEstudiante serviceEstudiante;

    @PostMapping("/register")
    public EstudianteDTO registerEstudiante(@RequestBody EstudianteDTO estudianteDTO) {
        return serviceEstudiante.save(estudianteDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody EstudianteDTO estudianteDTO) {
        EstudianteDTO estudiante = serviceEstudiante.findByCif(estudianteDTO.getCif());
        if (estudiante != null && estudianteDTO.getContrasena().equals(estudiante.getContrasena())) {
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @GetMapping
    public List<EstudianteDTO> getAllEstudiantes() {
        return serviceEstudiante.findAll();
    }

    @GetMapping("/{id}")
    public EstudianteDTO getEstudianteById(@PathVariable Long id) {
        return serviceEstudiante.findById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEstudiante(@PathVariable Long id) {
        serviceEstudiante.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
