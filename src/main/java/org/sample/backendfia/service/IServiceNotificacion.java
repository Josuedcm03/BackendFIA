package org.sample.backendfia.service;

import org.sample.backendfia.dto.NotificacionDTO;

import java.util.List;

public interface IServiceNotificacion {
    List<NotificacionDTO> findAll();
    NotificacionDTO findById(Long id);
    NotificacionDTO save(NotificacionDTO notificacionDTO);
    List<NotificacionDTO> findByEstudianteId(Long estudianteId);
}
