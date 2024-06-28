package org.sample.backendfia.controller;

import org.sample.backendfia.dto.EstudianteDTO;
import org.sample.backendfia.service.IServiceEstudiante;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/estudiantes")
public class EstudianteController {

    @Autowired
    private IServiceEstudiante serviceEstudiante;

    @GetMapping
    public List<EstudianteDTO> getAllEstudiantes() {
        return serviceEstudiante.findAll();
    }

    @GetMapping("/{id}")
    public EstudianteDTO getEstudianteById(@PathVariable Long id) {
        return serviceEstudiante.findById(id);
    }

    @PostMapping
    public EstudianteDTO createEstudiante(@RequestBody EstudianteDTO estudianteDTO) {
        return serviceEstudiante.save(estudianteDTO);
    }

    @PutMapping("/{id}")
    public EstudianteDTO updateEstudiante(@PathVariable Long id, @RequestBody EstudianteDTO estudianteDTO) {
        estudianteDTO.setId(id);
        return serviceEstudiante.save(estudianteDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEstudiante(@PathVariable Long id) {
        serviceEstudiante.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/register")
    public EstudianteDTO registerEstudiante(@RequestBody EstudianteDTO estudianteDTO) {
        return serviceEstudiante.register(estudianteDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<EstudianteDTO> loginEstudiante(@RequestBody Map<String, String> credentials) {
        String cif = credentials.get("cif");
        String contrasena = credentials.get("contrasena");
        EstudianteDTO estudianteDTO = serviceEstudiante.authenticate(cif, contrasena);
        return ResponseEntity.ok(estudianteDTO);
    }
}
