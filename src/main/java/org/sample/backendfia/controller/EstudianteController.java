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
    public ResponseEntity<EstudianteDTO> registerEstudiante(@RequestBody EstudianteDTO estudianteDTO) {
        EstudianteDTO registrado = serviceEstudiante.register(estudianteDTO);
        return new ResponseEntity<>(registrado, HttpStatus.CREATED);
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

    @PostMapping("/login")
    public ResponseEntity<EstudianteDTO> authenticateEstudiante(@RequestBody EstudianteDTO estudianteDTO) {
        EstudianteDTO authenticated = serviceEstudiante.authenticate(estudianteDTO.getCif(), estudianteDTO.getContrasena());
        return new ResponseEntity<>(authenticated, HttpStatus.OK);
    }

    @GetMapping("/cif/{cif}")
    public EstudianteDTO getEstudianteByCif(@PathVariable String cif) {
        return serviceEstudiante.findByCif(cif);
    }
}
