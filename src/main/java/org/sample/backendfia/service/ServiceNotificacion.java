package org.sample.backendfia.service;

import org.sample.backendfia.dto.NotificacionDTO;
import org.sample.backendfia.exception.ResourceNotFoundException;
import org.sample.backendfia.model.Notificacion;
import org.sample.backendfia.repository.NotificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceNotificacion implements IServiceNotificacion {

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public List<NotificacionDTO> findAll() {
        return notificacionRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public NotificacionDTO findById(Long id) {
        return notificacionRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Notificacion not found with id: " + id));
    }

    @Override
    public NotificacionDTO save(NotificacionDTO notificacionDTO) {
        Notificacion notificacion = convertToEntity(notificacionDTO);
        notificacion.setFechaEnvio(LocalDateTime.now());
        notificacion = notificacionRepository.save(notificacion);
        sendEmail(notificacion);
        return convertToDto(notificacion);
    }

    private void sendEmail(Notificacion notificacion) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(notificacion.getDestinatario());
        message.setSubject(notificacion.getAsunto());
        message.setText(notificacion.getMensaje());
        mailSender.send(message);
    }

    private NotificacionDTO convertToDto(Notificacion notificacion) {
        NotificacionDTO notificacionDTO = new NotificacionDTO();
        notificacionDTO.setId(notificacion.getId());
        notificacionDTO.setDestinatario(notificacion.getDestinatario());
        notificacionDTO.setAsunto(notificacion.getAsunto());
        notificacionDTO.setMensaje(notificacion.getMensaje());
        notificacionDTO.setFechaEnvio(notificacion.getFechaEnvio());
        return notificacionDTO;
    }

    private Notificacion convertToEntity(NotificacionDTO notificacionDTO) {
        Notificacion notificacion = new Notificacion();
        notificacion.setId(notificacionDTO.getId());
        notificacion.setDestinatario(notificacionDTO.getDestinatario());
        notificacion.setAsunto(notificacionDTO.getAsunto());
        notificacion.setMensaje(notificacionDTO.getMensaje());
        return notificacion;
    }
}
