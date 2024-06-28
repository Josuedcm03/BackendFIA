package org.sample.backendfia.service;

import org.sample.backendfia.dto.EstudianteDTO;
import org.sample.backendfia.exception.ResourceNotFoundException;
import org.sample.backendfia.model.Estudiante;
import org.sample.backendfia.repository.EstudianteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ServiceEstudiante implements IServiceEstudiante {

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Override
    public List<EstudianteDTO> findAll() {
        return estudianteRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public EstudianteDTO findById(Long id) {
        return estudianteRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante not found with id: " + id));
    }

    @Override
    public EstudianteDTO save(EstudianteDTO estudianteDTO) {
        Estudiante estudiante = convertToEntity(estudianteDTO);
        Estudiante savedEstudiante = estudianteRepository.save(estudiante);
        return convertToDto(savedEstudiante);
    }

    @Override
    public void deleteById(Long id) {
        estudianteRepository.deleteById(id);
    }

    @Override
    public EstudianteDTO register(EstudianteDTO estudianteDTO) {
        // Aquí podrías agregar lógica adicional como validar que el cif sea único
        Estudiante estudiante = convertToEntity(estudianteDTO);
        Estudiante savedEstudiante = estudianteRepository.save(estudiante);
        return convertToDto(savedEstudiante);
    }

    @Override
    public EstudianteDTO authenticate(String cif, String contrasena) {
        Optional<Estudiante> estudianteOpt = estudianteRepository.findByCifAndContrasena(cif, contrasena);
        if (estudianteOpt.isPresent()) {
            return convertToDto(estudianteOpt.get());
        } else {
            throw new IllegalArgumentException("Credenciales incorrectas");
        }
    }

    private EstudianteDTO convertToDto(Estudiante estudiante) {
        EstudianteDTO estudianteDTO = new EstudianteDTO();
        estudianteDTO.setId(estudiante.getId());
        estudianteDTO.setNombreCompleto(estudiante.getNombreCompleto());
        estudianteDTO.setCif(estudiante.getCif());
        estudianteDTO.setEmail(estudiante.getEmail());
        estudianteDTO.setContrasena(estudiante.getContrasena());
        return estudianteDTO;
    }

    private Estudiante convertToEntity(EstudianteDTO estudianteDTO) {
        Estudiante estudiante = new Estudiante();
        estudiante.setId(estudianteDTO.getId());
        estudiante.setNombreCompleto(estudianteDTO.getNombreCompleto());
        estudiante.setCif(estudianteDTO.getCif());
        estudiante.setEmail(estudianteDTO.getEmail());
        estudiante.setContrasena(estudianteDTO.getContrasena());
        return estudiante;
    }
}
