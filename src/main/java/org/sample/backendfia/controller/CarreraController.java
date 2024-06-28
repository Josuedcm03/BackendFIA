package org.sample.backendfia.controller;

import org.sample.backendfia.dto.CarreraDTO;
import org.sample.backendfia.service.IServiceCarrera;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carreras")
public class CarreraController {

    @Autowired
    private IServiceCarrera serviceCarrera;

    @GetMapping
    public List<CarreraDTO> getAllCarreras() {
        return serviceCarrera.findAll();
    }

    @GetMapping("/{id}")
    public CarreraDTO getCarreraById(@PathVariable Long id) {
        return serviceCarrera.findById(id);
    }

    @PostMapping
    public CarreraDTO createCarrera(@RequestBody CarreraDTO carreraDTO) {
        return serviceCarrera.save(carreraDTO);
    }

    @PutMapping("/{id}")
    public CarreraDTO updateCarrera(@PathVariable Long id, @RequestBody CarreraDTO carreraDTO) {
        carreraDTO.setId(id);
        return serviceCarrera.save(carreraDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCarrera(@PathVariable Long id) {
        serviceCarrera.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
