package org.sample.backendfia.service;

import org.sample.backendfia.dto.SolicitudDTO;
import org.sample.backendfia.exception.ResourceNotFoundException;
import org.sample.backendfia.model.Coordinador;
import org.sample.backendfia.model.Estudiante;
import org.sample.backendfia.model.Horario;
import org.sample.backendfia.model.Solicitud;
import org.sample.backendfia.repository.CoordinadorRepository;
import org.sample.backendfia.repository.EstudianteRepository;
import org.sample.backendfia.repository.HorarioRepository;
import org.sample.backendfia.repository.SolicitudRepository;
import org.sample.backendfia.util.DiaSemanaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.DayOfWeek;
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
    private HorarioRepository horarioRepository;

    @Autowired
    private JavaMailSender mailSender;

    private static final int DURACION_PERSONAL = 10;
    private static final int DURACION_FINANCIERO = 20;
    private static final int DURACION_ACADEMICO = 20;

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

        // Validar que la fecha no esté en el pasado
        if (solicitud.getFechaCita().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha de la cita no puede ser en el pasado.");
        }

        // Validar que la fecha sea de lunes a viernes
        DayOfWeek diaSemana = solicitud.getFechaCita().getDayOfWeek();
        if (diaSemana == DayOfWeek.SATURDAY || diaSemana == DayOfWeek.SUNDAY) {
            throw new IllegalArgumentException("La fecha de la cita debe ser un día hábil (lunes a viernes).");
        }

        // Asignar duración de la cita según el motivo
        switch (solicitudDTO.getMotivo().toLowerCase()) {
            case "personal":
                solicitud.setDuracionCita(DURACION_PERSONAL);
                break;
            case "financiero":
                solicitud.setDuracionCita(DURACION_FINANCIERO);
                break;
            case "academico":
                solicitud.setDuracionCita(DURACION_ACADEMICO);
                break;
            default:
                throw new IllegalArgumentException("Motivo no válido: " + solicitudDTO.getMotivo());
        }

        // Verificar disponibilidad
        List<Horario> horarios = horarioRepository.findAll();
        boolean isAvailable = horarios.stream().anyMatch(h ->
                !solicitud.getFechaCita().toLocalTime().isBefore(h.getHoraInicio()) &&
                        !solicitud.getFechaCita().toLocalTime().plusMinutes(solicitud.getDuracionCita()).isAfter(h.getHoraFin()) &&
                        solicitud.getFechaCita().getDayOfWeek().equals(h.getDiaSemana()) &&
                        h.getEstado().equals("libre")
        );

        if (!isAvailable) {
            throw new IllegalArgumentException("El coordinador no está disponible en el horario solicitado.");
        }

        solicitud.setEstudiante(estudiante);
        solicitud.setCoordinador(coordinador);
        Solicitud savedSolicitud = solicitudRepository.save(solicitud);

        // Actualizar el estado del horario
        horarios.stream().filter(h ->
                !solicitud.getFechaCita().toLocalTime().isBefore(h.getHoraInicio()) &&
                        !solicitud.getFechaCita().toLocalTime().plusMinutes(solicitud.getDuracionCita()).isAfter(h.getHoraFin()) &&
                        solicitud.getFechaCita().getDayOfWeek().equals(h.getDiaSemana())
        ).forEach(h -> {
            h.setEstado("cita");
            horarioRepository.save(h);
        });

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
            // Validar que la fecha no esté en el pasado
            if (nuevaFecha.isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("La fecha de la cita no puede ser en el pasado.");
            }

            // Validar que la fecha sea de lunes a viernes
            DayOfWeek diaSemana = nuevaFecha.getDayOfWeek();
            if (diaSemana == DayOfWeek.SATURDAY || diaSemana == DayOfWeek.SUNDAY) {
                throw new IllegalArgumentException("La fecha de la cita debe ser un día hábil (lunes a viernes).");
            }

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
        String asunto = "Cambio de estado de su solicitud";
        String mensajeEstudiante = "Su solicitud ha cambiado al estado: " + solicitud.getEstado();
        String mensajeCoordinador = "La solicitud de cita ha sido aceptada para el estudiante: " + solicitud.getEstudiante().getNombreCompleto();

        enviarCorreo(solicitud.getEstudiante().getEmail(), asunto, mensajeEstudiante);

        if (solicitud.getEstado().equals(Solicitud.APROBADA)) {
            enviarCorreo(solicitud.getCoordinador().getEmail(), asunto, mensajeCoordinador);
        }
    }

    private void enviarCorreo(String destinatario, String asunto, String mensaje) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(destinatario);
        email.setSubject(asunto);
        email.setText(mensaje);
        mailSender.send(email);
    }

    private SolicitudDTO convertToDto(Solicitud solicitud) {
        SolicitudDTO solicitudDTO = new SolicitudDTO();
        solicitudDTO.setId(solicitud.getId());
        solicitudDTO.setEstado(solicitud.getEstado());
        solicitudDTO.setFechaSolicitud(solicitud.getFechaSolicitud());
        solicitudDTO.setFechaCita(solicitud.getFechaCita());
        solicitudDTO.setEstudianteId(solicitud.getEstudiante().getId());
        solicitudDTO.setCoordinadorId(solicitud.getCoordinador().getId());
        solicitudDTO.setMotivo(solicitud.getMotivo());
        solicitudDTO.setDuracionCita(solicitud.getDuracionCita());
        solicitudDTO.setDiaSemana(DiaSemanaUtil.getDiaSemanaEnEspanol(solicitud.getFechaCita().getDayOfWeek())); // Asignar el día de la semana en español
        return solicitudDTO;
    }

    private Solicitud convertToEntity(SolicitudDTO solicitudDTO) {
        Solicitud solicitud = new Solicitud();
        solicitud.setId(solicitudDTO.getId());
        solicitud.setEstado(solicitudDTO.getEstado());
        solicitud.setFechaSolicitud(solicitudDTO.getFechaSolicitud());
        solicitud.setFechaCita(solicitudDTO.getFechaCita());
        solicitud.setMotivo(solicitudDTO.getMotivo());
        solicitud.setDuracionCita(solicitudDTO.getDuracionCita());
        return solicitud;
    }
}
