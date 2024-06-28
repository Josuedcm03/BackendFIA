package org.sample.backendfia.service;

import org.sample.backendfia.dto.SecretariaDTO;
import org.sample.backendfia.dto.SolicitudDTO;
import org.sample.backendfia.exception.ResourceNotFoundException;
import org.sample.backendfia.model.Secretaria;
import org.sample.backendfia.repository.SecretariaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceSecretaria implements IServiceSecretaria {

    @Autowired
    private SecretariaRepository secretariaRepository;

    @Autowired
    private IServiceSolicitud serviceSolicitud;

    @Override
    public List<SecretariaDTO> findAll() {
        return secretariaRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public SecretariaDTO findById(Long id) {
        return secretariaRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Secretaria not found with id: " + id));
    }

    @Override
    public SecretariaDTO save(SecretariaDTO secretariaDTO) {
        Secretaria secretaria = convertToEntity(secretariaDTO);
        Secretaria savedSecretaria = secretariaRepository.save(secretaria);
        return convertToDto(savedSecretaria);
    }

    @Override
    public void deleteById(Long id) {
        secretariaRepository.deleteById(id);
    }

    @Override
    public SolicitudDTO cambiarEstadoSolicitud(Long solicitudId, String nuevoEstado) {
        return serviceSolicitud.cambiarEstado(solicitudId, nuevoEstado);
    }

    private SecretariaDTO convertToDto(Secretaria secretaria) {
        SecretariaDTO secretariaDTO = new SecretariaDTO();
        secretariaDTO.setId(secretaria.getId());
        secretariaDTO.setNombre(secretaria.getNombre());
        secretariaDTO.setEmail(secretaria.getEmail());
        return secretariaDTO;
    }

    private Secretaria convertToEntity(SecretariaDTO secretariaDTO) {
        Secretaria secretaria = new Secretaria();
        secretaria.setId(secretariaDTO.getId());
        secretaria.setNombre(secretariaDTO.getNombre());
        secretaria.setEmail(secretariaDTO.getEmail());
        return secretaria;
    }
}
