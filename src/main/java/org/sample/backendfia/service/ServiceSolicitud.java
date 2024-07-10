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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class ServiceSolicitud implements IServiceSolicitud {

    @Autowired
    private SolicitudRepository solicitudRepository;

    @Autowired
    private CoordinadorRepository coordinadorRepository;

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private HorarioRepository horarioRepository;

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

        // Verificar disponibilidad del coordinador
        List<Horario> horarios = horarioRepository.findByCoordinadorIdAndDiaSemanaAndHoraInicio(
                solicitud.getCoordinador().getId(),
                solicitud.getFechaCita().getDayOfWeek(),
                solicitud.getFechaCita().toLocalTime()
        );

        if (horarios.isEmpty() || !horarios.get(0).getEstado().equals("libre")) {
            throw new IllegalArgumentException("El coordinador no está disponible en el horario solicitado.");
        }

        // Validar el motivo
        if (solicitud.getMotivo() == null) {
            throw new IllegalArgumentException("El motivo de la cita es obligatorio.");
        }

        // Cambiar el estado del horario a ocupado
        Horario horario = horarios.get(0);
        horario.setEstado("ocupado");
        horarioRepository.save(horario);

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

        // Verificar disponibilidad del coordinador para la nueva fecha
        List<Horario> horarios = horarioRepository.findByCoordinadorIdAndDiaSemanaAndHoraInicio(
                solicitud.getCoordinador().getId(),
                nuevaFecha.getDayOfWeek(),
                nuevaFecha.toLocalTime()
        );

        if (horarios.isEmpty() || !horarios.get(0).getEstado().equals("libre")) {
            throw new IllegalArgumentException("El coordinador no está disponible en el nuevo horario solicitado.");
        }

        // Cambiar el estado del horario anterior a libre
        Horario horarioAnterior = horarioRepository.findByCoordinadorIdAndDiaSemanaAndHoraInicio(
                solicitud.getCoordinador().getId(),
                solicitud.getFechaCita().getDayOfWeek(),
                solicitud.getFechaCita().toLocalTime()
        ).get(0);
        horarioAnterior.setEstado("libre");
        horarioRepository.save(horarioAnterior);

        // Cambiar el estado del nuevo horario a ocupado
        Horario nuevoHorario = horarios.get(0);
        nuevoHorario.setEstado("ocupado");
        horarioRepository.save(nuevoHorario);

        solicitud.setFechaCita(nuevaFecha);
        Solicitud updatedSolicitud = solicitudRepository.save(solicitud);
        return convertToDto(updatedSolicitud);
    }

    @Override
    public SolicitudDTO cambiarCoordinador(Long id, Long nuevoCoordinadorId) {
        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud not found with id: " + id));

        Coordinador nuevoCoordinador = coordinadorRepository.findById(nuevoCoordinadorId)
                .orElseThrow(() -> new ResourceNotFoundException("Coordinador not found with id: " + nuevoCoordinadorId));

        // Verificar disponibilidad del nuevo coordinador
        List<Horario> horarios = horarioRepository.findByCoordinadorIdAndDiaSemanaAndHoraInicio(
                nuevoCoordinadorId,
                solicitud.getFechaCita().getDayOfWeek(),
                solicitud.getFechaCita().toLocalTime()
        );

        if (horarios.isEmpty() || !horarios.get(0).getEstado().equals("libre")) {
            throw new IllegalArgumentException("El nuevo coordinador no está disponible en el horario solicitado.");
        }

        solicitud.setCoordinador(nuevoCoordinador);
        Solicitud updatedSolicitud = solicitudRepository.save(solicitud);
        return convertToDto(updatedSolicitud);
    }

    @Override
    public void cancelarCita(Long id) {
        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud not found with id: " + id));

        // Cambiar el estado del horario a libre
        Horario horario = horarioRepository.findByCoordinadorIdAndDiaSemanaAndHoraInicio(
                solicitud.getCoordinador().getId(),
                solicitud.getFechaCita().getDayOfWeek(),
                solicitud.getFechaCita().toLocalTime()
        ).get(0);
        horario.setEstado("libre");
        horarioRepository.save(horario);

        solicitud.setEstado(Solicitud.CANCELADA);
        solicitudRepository.save(solicitud);
    }

    @Override
    public SolicitudDTO cambiarEstado(Long id, String nuevoEstado) {
        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud not found with id: " + id));

        solicitud.setEstado(nuevoEstado);
        Solicitud updatedSolicitud = solicitudRepository.save(solicitud);
        return convertToDto(updatedSolicitud);
    }

    @Override
    public List<SolicitudDTO> findByEstudianteId(Long estudianteId) {
        return solicitudRepository.findByEstudianteId(estudianteId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
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
        solicitudDTO.setDescripcionMotivo(solicitud.getDescripcionMotivo());

        // Establecer el día de la semana en español
        solicitudDTO.setDiaSemana(solicitud.getFechaCita().getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("es", "ES")));

        return solicitudDTO;
    }

    private Solicitud convertToEntity(SolicitudDTO solicitudDTO) {
        Solicitud solicitud = new Solicitud();
        solicitud.setId(solicitudDTO.getId());
        solicitud.setEstado(solicitudDTO.getEstado());
        solicitud.setFechaSolicitud(solicitudDTO.getFechaSolicitud());
        solicitud.setFechaCita(solicitudDTO.getFechaCita());

        // Validar que el ID del coordinador no sea nulo
        if (solicitudDTO.getCoordinadorId() == null) {
            throw new IllegalArgumentException("Coordinador ID no debe ser null");
        }
        Coordinador coordinador = coordinadorRepository.findById(solicitudDTO.getCoordinadorId())
                .orElseThrow(() -> new ResourceNotFoundException("Coordinador no encontrado con id: " + solicitudDTO.getCoordinadorId()));
        solicitud.setCoordinador(coordinador);

        // Validar que el ID del estudiante no sea nulo
        if (solicitudDTO.getEstudianteId() == null) {
            throw new IllegalArgumentException("Estudiante ID must not be null");
        }
        Estudiante estudiante = estudianteRepository.findById(solicitudDTO.getEstudianteId())
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante not found with id: " + solicitudDTO.getEstudianteId()));
        solicitud.setEstudiante(estudiante);

        solicitud.setMotivo(solicitudDTO.getMotivo());
        solicitud.setDuracionCita(solicitudDTO.getDuracionCita());
        solicitud.setDescripcionMotivo(solicitudDTO.getDescripcionMotivo());
        return solicitud;
    }

}
