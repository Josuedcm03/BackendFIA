package org.sample.backendfia.service;

import org.sample.backendfia.dto.SolicitudDTO;
import org.sample.backendfia.model.Horario;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface IServiceSolicitud {
    List<SolicitudDTO> findAll();
    SolicitudDTO findById(Long id);
    SolicitudDTO save(SolicitudDTO solicitudDTO);
    void deleteById(Long id);
    SolicitudDTO updateFechaCita(Long id, LocalDate fecha, LocalTime hora);
    SolicitudDTO cambiarCoordinador(Long id, Long nuevoCoordinadorId);
    void cancelarCita(Long id);
    SolicitudDTO cambiarEstado(Long id, String nuevoEstado);
    List<SolicitudDTO> findByEstudianteId(Long estudianteId);

}
