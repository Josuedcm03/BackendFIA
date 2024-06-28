package org.sample.backendfia.controller;

import org.sample.backendfia.dto.NotificacionDTO;
import org.sample.backendfia.service.IServiceNotificacion;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    @Autowired
    private IServiceNotificacion serviceNotificacion;

    @GetMapping
    public List<NotificacionDTO> getAllNotificaciones() {
        return serviceNotificacion.findAll();
    }

    @GetMapping("/{id}")
    public NotificacionDTO getNotificacionById(@PathVariable Long id) {
        return serviceNotificacion.findById(id);
    }

    @PostMapping
    public NotificacionDTO createNotificacion(@RequestBody NotificacionDTO notificacionDTO) {
        return serviceNotificacion.save(notificacionDTO);
    }
}