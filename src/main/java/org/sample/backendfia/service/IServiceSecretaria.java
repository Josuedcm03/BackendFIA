package org.sample.backendfia.service;

import org.sample.backendfia.dto.SecretariaDTO;
import org.sample.backendfia.dto.SolicitudDTO;

import java.util.List;

public interface IServiceSecretaria {
    List<SecretariaDTO> findAll();
    SecretariaDTO findById(Long id);
    SecretariaDTO save(SecretariaDTO secretariaDTO);
    void deleteById(Long id);
    SolicitudDTO cambiarEstadoSolicitud(Long solicitudId, String nuevoEstado);
}
