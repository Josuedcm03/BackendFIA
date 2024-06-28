package org.sample.backendfia.service;

import org.sample.backendfia.dto.SolicitudDTO;
import org.sample.backendfia.exception.ResourceNotFoundException;
import org.sample.backendfia.model.Coordinador;
import org.sample.backendfia.model.Estudiante;
import org.sample.backendfia.model.Notificacion;
import org.sample.backendfia.model.Solicitud;
import org.sample.backendfia.repository.CoordinadorRepository;
import org.sample.backendfia.repository.EstudianteRepository;
import org.sample.backendfia.repository.NotificacionRepository;
import org.sample.backendfia.repository.SolicitudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceSolicitud implements IServiceSolicitud {

    @Autowired
    private SolicitudRepository solicitudRepository;

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private CoordinadorRepository coordinadorRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Override
    public List<SolicitudDTO> findAll() {
        return solicitudRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public SolicitudDTO findById(Long id) {
        return solicitudRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud not found with id: " + id));
    }

    @Override
    public SolicitudDTO save(SolicitudDTO solicitudDTO) {
        Solicitud solicitud = convertToEntity(solicitudDTO);
        Estudiante estudiante = estudianteRepository.findById(solicitudDTO.getEstudianteId())
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante not found with id: " + solicitudDTO.getEstudianteId()));
        Coordinador coordinador = coordinadorRepository.findById(solicitudDTO.getCoordinadorId())
                .orElseThrow(() -> new ResourceNotFoundException("Coordinador not found with id: " + solicitudDTO.getCoordinadorId()));
        solicitud.setEstudiante(estudiante);
        solicitud.setCoordinador(coordinador);
        Solicitud savedSolicitud = solicitudRepository.save(solicitud);
        return convertToDto(savedSolicitud);
    }

    @Override
    public void deleteById(Long id) {
        solicitudRepository.deleteById(id);
    }

    @Override
    public SolicitudDTO updateFechaCita(Long id, LocalDateTime nuevaFecha) {
        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud not found with id: " + id));

        if (!solicitud.getEstado().equalsIgnoreCase(Solicitud.APROBADA)) {
            solicitud.setFechaCita(nuevaFecha);
            solicitudRepository.save(solicitud);
        } else {
            throw new IllegalStateException("No se puede modificar la fecha de una cita aprobada");
        }
        return convertToDto(solicitud);
    }

    @Override
    public SolicitudDTO cambiarCoordinador(Long id, Long nuevoCoordinadorId) {
        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud not found with id: " + id));

        if (!solicitud.getEstado().equalsIgnoreCase(Solicitud.APROBADA)) {
            Coordinador nuevoCoordinador = coordinadorRepository.findById(nuevoCoordinadorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Coordinador not found with id: " + nuevoCoordinadorId));
            solicitud.setCoordinador(nuevoCoordinador);
            solicitudRepository.save(solicitud);
        } else {
            throw new IllegalStateException("No se puede cambiar el coordinador de una cita aprobada");
        }
        return convertToDto(solicitud);
    }

    @Override
    public void cancelarCita(Long id) {
        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud not found with id: " + id));

        solicitud.setEstado(Solicitud.CANCELADA);
        solicitudRepository.save(solicitud);
    }

    @Override
    public SolicitudDTO cambiarEstado(Long id, String nuevoEstado) {
        if (!List.of(Solicitud.PENDIENTE, Solicitud.APROBADA, Solicitud.RECHAZADA, Solicitud.CANCELADA, Solicitud.REPROGRAMADA).contains(nuevoEstado)) {
            throw new IllegalArgumentException("Estado no válido: " + nuevoEstado);
        }

        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud not found with id: " + id));

        solicitud.setEstado(nuevoEstado);
        solicitudRepository.save(solicitud);
        enviarCorreoCambioEstado(solicitud);
        return convertToDto(solicitud);
    }

    private void enviarCorreoCambioEstado(Solicitud solicitud) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(solicitud.getEstudiante().getEmail());
        message.setSubject("Cambio de estado de su solicitud");
        message.setText("Su solicitud ha cambiado al estado: " + solicitud.getEstado());
        mailSender.send(message);

        // Guardar la notificación en la base de datos
        Notificacion notificacion = new Notificacion();
        notificacion.setDestinatario(solicitud.getEstudiante().getEmail());
        notificacion.setAsunto("Cambio de estado de su solicitud");
        notificacion.setMensaje("Su solicitud ha cambiado al estado: " + solicitud.getEstado());
        notificacion.setFechaEnvio(LocalDateTime.now());
        notificacionRepository.save(notificacion);
    }

    private SolicitudDTO convertToDto(Solicitud solicitud) {
        SolicitudDTO solicitudDTO = new SolicitudDTO();
        solicitudDTO.setId(solicitud.getId());
        solicitudDTO.setEstado(solicitud.getEstado());
        solicitudDTO.setFechaSolicitud(solicitud.getFechaSolicitud());
        solicitudDTO.setFechaCita(solicitud.getFechaCita());
        solicitudDTO.setEstudianteId(solicitud.getEstudiante().getId());
        solicitudDTO.setCoordinadorId(solicitud.getCoordinador().getId());
        return solicitudDTO;
    }

    private Solicitud convertToEntity(SolicitudDTO solicitudDTO) {
        Solicitud solicitud = new Solicitud();
        solicitud.setId(solicitudDTO.getId());
        solicitud.setEstado(solicitudDTO.getEstado());
        solicitud.setFechaSolicitud(solicitudDTO.getFechaSolicitud());
        solicitud.setFechaCita(solicitudDTO.getFechaCita());
        return solicitud;
    }
}
