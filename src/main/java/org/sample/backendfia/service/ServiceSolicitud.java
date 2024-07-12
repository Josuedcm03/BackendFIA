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

import java.time.LocalDate;
import java.time.LocalTime;
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

        // Verificar disponibilidad del horario
        List<Horario> horarios = horarioRepository.findByCoordinadorIdAndFechaAndHoraInicio(
                solicitudDTO.getCoordinadorId(),
                solicitudDTO.getFecha(),
                solicitudDTO.getHora()
        );

        if (horarios.isEmpty()) {
            throw new ResourceNotFoundException("No se encontró un horario disponible para el coordinador y el tiempo seleccionado.");
        }

        Horario horarioSeleccionado = horarios.get(0);

        if (horarioSeleccionado.getEstado().equals("ocupado")) {
            throw new IllegalArgumentException("El horario no está disponible.");
        }

        // Asignar el ID del horario seleccionado
        solicitud.setHorario(horarioSeleccionado);

        // Validar el motivo
        if (solicitud.getMotivo() == null) {
            throw new IllegalArgumentException("El motivo de la cita es obligatorio.");
        }

        // Guardar la solicitud sin cambiar el estado del horario
        Solicitud savedSolicitud = solicitudRepository.save(solicitud);
        return convertToDto(savedSolicitud);
    }

    @Override
    public void deleteById(Long id) {
        solicitudRepository.deleteById(id);
    }

    @Override
    public SolicitudDTO updateFechaCita(Long id, LocalDate fecha, LocalTime hora) {
        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud not found with id: " + id));

        // Verificar disponibilidad del coordinador para la nueva fecha y hora
        List<Horario> horarios = horarioRepository.findByCoordinadorIdAndFechaAndHoraInicio(
                solicitud.getCoordinador().getId(),
                fecha,
                hora
        );

        if (horarios.isEmpty() || horarios.get(0).getEstado().equals("ocupado")) {
            throw new IllegalArgumentException("El coordinador no está disponible en el nuevo horario solicitado.");
        }

        // Cambiar el estado del horario anterior a libre
        Horario horarioAnterior = solicitud.getHorario();
        if (horarioAnterior != null) {
            horarioAnterior.setEstado("libre");
            horarioRepository.save(horarioAnterior);
        }

        // Asignar el nuevo horario y no cambiar el estado del nuevo horario aquí
        Horario nuevoHorario = horarios.get(0);
        solicitud.setFecha(fecha);
        solicitud.setHora(hora);
        solicitud.setHorario(nuevoHorario);
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
        List<Horario> horarios = horarioRepository.findByCoordinadorIdAndFechaAndHoraInicio(
                nuevoCoordinadorId,
                solicitud.getFecha(),
                solicitud.getHora()
        );

        if (horarios.isEmpty() || horarios.get(0).getEstado().equals("ocupado")) {
            throw new IllegalArgumentException("El nuevo coordinador no está disponible en el horario solicitado.");
        }

        solicitud.setCoordinador(nuevoCoordinador);
        solicitud.setHorario(horarios.get(0));
        Solicitud updatedSolicitud = solicitudRepository.save(solicitud);
        return convertToDto(updatedSolicitud);
    }

    @Override
    public void cancelarCita(Long id) {
        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud not found with id: " + id));

        // Cambiar el estado del horario a libre
        Horario horario = solicitud.getHorario();
        if (horario != null) {
            horario.setEstado("libre");
            horarioRepository.save(horario);
        }

        solicitud.setEstado(Solicitud.CANCELADA);
        solicitudRepository.save(solicitud);
    }

    @Override
    public SolicitudDTO cambiarEstado(Long id, String nuevoEstado) {
        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud not found with id: " + id));

        Horario horario = solicitud.getHorario();

        if (nuevoEstado.equals(Solicitud.APROBADA)) {
            // Cambiar el estado del horario a ocupado si la solicitud es aprobada
            if (horario != null) {
                horario.setEstado("ocupado");
                horarioRepository.save(horario);
            }
        } else if (nuevoEstado.equals(Solicitud.RECHAZADA) || nuevoEstado.equals(Solicitud.CANCELADA)) {
            // Cambiar el estado del horario a libre si la solicitud es rechazada o cancelada
            if (horario != null) {
                horario.setEstado("libre");
                horarioRepository.save(horario);
            }
        }

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
        solicitudDTO.setFecha(solicitud.getFecha());
        solicitudDTO.setHora(solicitud.getHora());
        solicitudDTO.setEstudianteId(solicitud.getEstudiante().getId());
        solicitudDTO.setCoordinadorId(solicitud.getCoordinador().getId());
        solicitudDTO.setHorarioId(solicitud.getHorario() != null ? solicitud.getHorario().getId() : null);
        solicitudDTO.setMotivo(solicitud.getMotivo());
        solicitudDTO.setDuracionCita(solicitud.getDuracionCita());
        solicitudDTO.setDescripcionMotivo(solicitud.getDescripcionMotivo());

        // Establecer el día de la semana en español
        solicitudDTO.setDiaSemana(solicitud.getFecha().getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("es", "ES")));

        return solicitudDTO;
    }

    private Solicitud convertToEntity(SolicitudDTO solicitudDTO) {
        Solicitud solicitud = new Solicitud();
        solicitud.setId(solicitudDTO.getId());
        solicitud.setEstado(solicitudDTO.getEstado());
        solicitud.setFechaSolicitud(solicitudDTO.getFechaSolicitud());
        solicitud.setFecha(solicitudDTO.getFecha());
        solicitud.setHora(solicitudDTO.getHora());

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

        // Asignar el horario basado en la disponibilidad del coordinador, la fecha y la hora
        List<Horario> horarios = horarioRepository.findByCoordinadorIdAndFechaAndHoraInicio(
                solicitudDTO.getCoordinadorId(),
                solicitudDTO.getFecha(),
                solicitudDTO.getHora()
        );

        if (horarios.isEmpty()) {
            throw new ResourceNotFoundException("No se encontró un horario disponible para el coordinador y el tiempo seleccionado.");
        }

        Horario horarioSeleccionado = horarios.get(0);
        solicitud.setHorario(horarioSeleccionado);

        solicitud.setMotivo(solicitudDTO.getMotivo());
        solicitud.setDuracionCita(solicitudDTO.getDuracionCita());
        solicitud.setDescripcionMotivo(solicitudDTO.getDescripcionMotivo());
        return solicitud;
    }
}
