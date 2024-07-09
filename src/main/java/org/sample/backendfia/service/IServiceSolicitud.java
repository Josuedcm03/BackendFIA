package org.sample.backendfia.service;

import org.sample.backendfia.dto.SolicitudDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface IServiceSolicitud {
    List<SolicitudDTO> findAll();
    SolicitudDTO findById(Long id);
    SolicitudDTO save(SolicitudDTO solicitudDTO);
    void deleteById(Long id);
    SolicitudDTO updateFechaCita(Long id, LocalDateTime nuevaFecha);
    SolicitudDTO cambiarCoordinador(Long id, Long nuevoCoordinadorId);
    void cancelarCita(Long id);
    SolicitudDTO cambiarEstado(Long id, String nuevoEstado);
    List<SolicitudDTO> findByEstudianteId(Long estudianteId);
}
