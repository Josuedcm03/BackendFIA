package org.sample.backendfia.service;

import org.sample.backendfia.dto.CarreraDTO;
import org.sample.backendfia.exception.ResourceNotFoundException;
import org.sample.backendfia.model.Carrera;
import org.sample.backendfia.repository.CarreraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceCarrera implements IServiceCarrera {

    @Autowired
    private CarreraRepository carreraRepository;

    @Override
    public List<CarreraDTO> findAll() {
        return carreraRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public CarreraDTO findById(Long id) {
        return carreraRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Carrera not found with id: " + id));
    }

    @Override
    public CarreraDTO save(CarreraDTO carreraDTO) {
        Carrera carrera = convertToEntity(carreraDTO);
        Carrera savedCarrera = carreraRepository.save(carrera);
        return convertToDto(savedCarrera);
    }

    @Override
    public void deleteById(Long id) {
        carreraRepository.deleteById(id);
    }

    private CarreraDTO convertToDto(Carrera carrera) {
        CarreraDTO carreraDTO = new CarreraDTO();
        carreraDTO.setId(carrera.getId());
        carreraDTO.setNombre(carrera.getNombre());
        return carreraDTO;
    }

    private Carrera convertToEntity(CarreraDTO carreraDTO) {
        Carrera carrera = new Carrera();
        carrera.setId(carreraDTO.getId());
        carrera.setNombre(carreraDTO.getNombre());
        return carrera;
    }
}
