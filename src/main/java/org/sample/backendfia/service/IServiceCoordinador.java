package org.sample.backendfia.service;

import org.sample.backendfia.dto.CoordinadorDTO;
import org.sample.backendfia.dto.HorarioDTO;
import org.sample.backendfia.dto.SolicitudDTO;

import java.util.List;

public interface IServiceCoordinador {
    List<CoordinadorDTO> findAll();
    CoordinadorDTO findById(Long id);
    CoordinadorDTO save(CoordinadorDTO coordinadorDTO);
    void deleteById(Long id);
    List<HorarioDTO> getDisponibilidad(Long coordinadorId);
    CoordinadorDTO findByEmail(String email);
    List<SolicitudDTO> getSolicitudesByCoordinadorId(Long coordinadorId); // Añadir esta línea
}
