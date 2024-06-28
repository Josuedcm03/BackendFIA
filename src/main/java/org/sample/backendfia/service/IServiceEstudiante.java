package org.sample.backendfia.service;

import org.sample.backendfia.dto.EstudianteDTO;

import java.util.List;

public interface IServiceEstudiante {
    List<EstudianteDTO> findAll();
    EstudianteDTO findById(Long id);
    EstudianteDTO save(EstudianteDTO estudianteDTO);
    void deleteById(Long id);
    EstudianteDTO register(EstudianteDTO estudianteDTO);
    EstudianteDTO authenticate(String cif, String contrasena);
}
