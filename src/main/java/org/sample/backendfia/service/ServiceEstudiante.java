package org.sample.backendfia.service;

import org.sample.backendfia.dto.EstudianteDTO;
import org.sample.backendfia.exception.ResourceNotFoundException;
import org.sample.backendfia.model.Carrera;
import org.sample.backendfia.model.Estudiante;
import org.sample.backendfia.repository.CarreraRepository;
import org.sample.backendfia.repository.EstudianteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceEstudiante implements IServiceEstudiante {

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private CarreraRepository carreraRepository;

    private static final List<String> CARRERAS_PERMITIDAS = Arrays.asList(
            "Ingenieria en sistemas de la informacion",
            "Ingenieria industrial",
            "Ingenieria civil",
            "Arquitectura"
    );

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
    public EstudianteDTO register(EstudianteDTO estudianteDTO) {
        return save(estudianteDTO);
    }

    @Override
    public EstudianteDTO authenticate(String cif, String contrasena) {
        return estudianteRepository.findByCifAndContrasena(cif, contrasena)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid CIF or password"));
    }

    @Override
    public void deleteById(Long id) {
        estudianteRepository.deleteById(id);
    }

    @Override
    public EstudianteDTO findByCif(String cif) {
        return estudianteRepository.findByCif(cif)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante not found with CIF: " + cif));
    }

    @Override
    public EstudianteDTO save(EstudianteDTO estudianteDTO) {
        if (!estudianteDTO.getContrasena().equals(estudianteDTO.getConfirmarContrasena())) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }

        if (!CARRERAS_PERMITIDAS.contains(estudianteDTO.getCarrera())) {
            throw new IllegalArgumentException("Carrera no permitida");
        }

        Carrera carrera = carreraRepository.findByNombre(estudianteDTO.getCarrera())
                .orElseThrow(() -> new ResourceNotFoundException("Carrera not found with name: " + estudianteDTO.getCarrera()));

        Estudiante estudiante = convertToEntity(estudianteDTO);
        estudiante.setCarrera(carrera.getNombre());

        Estudiante savedEstudiante = estudianteRepository.save(estudiante);
        return convertToDto(savedEstudiante);
    }

    private EstudianteDTO convertToDto(Estudiante estudiante) {
        EstudianteDTO estudianteDTO = new EstudianteDTO();
        estudianteDTO.setId(estudiante.getId());
        estudianteDTO.setNombreCompleto(estudiante.getNombre());
        estudianteDTO.setCif(estudiante.getCif());
        estudianteDTO.setEmail(estudiante.getEmail());
        estudianteDTO.setContrasena(estudiante.getContrasena());
        estudianteDTO.setCarrera(estudiante.getCarrera());
        return estudianteDTO;
    }

    private Estudiante convertToEntity(EstudianteDTO estudianteDTO) {
        Estudiante estudiante = new Estudiante();
        estudiante.setId(estudianteDTO.getId());
        estudiante.setNombre(estudianteDTO.getNombreCompleto());
        estudiante.setCif(estudianteDTO.getCif());
        estudiante.setEmail(estudianteDTO.getEmail());
        estudiante.setContrasena(estudianteDTO.getContrasena());
        estudiante.setCarrera(estudianteDTO.getCarrera());
        return estudiante;
    }
}
