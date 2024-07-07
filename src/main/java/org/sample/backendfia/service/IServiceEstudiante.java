package org.sample.backendfia.service;

import org.sample.backendfia.dto.EstudianteDTO;

import java.util.List;

public interface IServiceEstudiante {
    List<EstudianteDTO> findAll();
    EstudianteDTO findById(Long id);
    EstudianteDTO register(EstudianteDTO estudianteDTO);
    EstudianteDTO authenticate(String cif, String contrasena);
    void deleteById(Long id);
    EstudianteDTO findByCif(String cif);
    EstudianteDTO save(EstudianteDTO estudianteDTO);  // Añadir esta línea
}
