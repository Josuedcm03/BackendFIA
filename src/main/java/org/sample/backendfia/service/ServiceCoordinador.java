package org.sample.backendfia.service;

import org.sample.backendfia.dto.CoordinadorDTO;
import org.sample.backendfia.exception.ResourceNotFoundException;
import org.sample.backendfia.model.Coordinador;
import org.sample.backendfia.repository.CoordinadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceCoordinador implements IServiceCoordinador {

    @Autowired
    private CoordinadorRepository coordinadorRepository;

    @Override
    public List<CoordinadorDTO> findAll() {
        return coordinadorRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public CoordinadorDTO findById(Long id) {
        return coordinadorRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Coordinador not found with id: " + id));
    }

    @Override
    public CoordinadorDTO save(CoordinadorDTO coordinadorDTO) {
        Coordinador coordinador = convertToEntity(coordinadorDTO);
        Coordinador savedCoordinador = coordinadorRepository.save(coordinador);
        return convertToDto(savedCoordinador);
    }

    @Override
    public void deleteById(Long id) {
        coordinadorRepository.deleteById(id);
    }

    private CoordinadorDTO convertToDto(Coordinador coordinador) {
        CoordinadorDTO coordinadorDTO = new CoordinadorDTO();
        coordinadorDTO.setId(coordinador.getId());
        coordinadorDTO.setNombre(coordinador.getNombre());
        coordinadorDTO.setEmail(coordinador.getEmail());
        return coordinadorDTO;
    }

    private Coordinador convertToEntity(CoordinadorDTO coordinadorDTO) {
        Coordinador coordinador = new Coordinador();
        coordinador.setId(coordinadorDTO.getId());
        coordinador.setNombre(coordinadorDTO.getNombre());
        coordinador.setEmail(coordinadorDTO.getEmail());
        return coordinador;
    }
}
