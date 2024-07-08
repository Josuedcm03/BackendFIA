package org.sample.backendfia.service;

import org.sample.backendfia.dto.CoordinadorDTO;
import org.sample.backendfia.dto.HorarioDTO;
import org.sample.backendfia.exception.ResourceNotFoundException;
import org.sample.backendfia.model.Carrera;
import org.sample.backendfia.model.Coordinador;
import org.sample.backendfia.model.Horario;
import org.sample.backendfia.repository.CarreraRepository;
import org.sample.backendfia.repository.CoordinadorRepository;
import org.sample.backendfia.repository.HorarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceCoordinador implements IServiceCoordinador {

    @Autowired
    private CoordinadorRepository coordinadorRepository;

    @Autowired
    private HorarioRepository horarioRepository;

    @Autowired
    private CarreraRepository carreraRepository;

    @Override
    public List<CoordinadorDTO> findAll() {
        return coordinadorRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public CoordinadorDTO findById(Long id) {
        return coordinadorRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Coordinador not found with id: " + id));
    }

    @Override
    public CoordinadorDTO findByEmail(String email) {
        return coordinadorRepository.findByEmail(email)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Coordinador not found with email: " + email));
    }

    @Override
    public CoordinadorDTO save(CoordinadorDTO coordinadorDTO) {
        if (!coordinadorDTO.getContrasena().equals(coordinadorDTO.getConfirmarContrasena())) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }

        Carrera carrera = carreraRepository.findById(coordinadorDTO.getCarreraId())
                .orElseThrow(() -> new ResourceNotFoundException("Carrera not found with id: " + coordinadorDTO.getCarreraId()));

        Coordinador coordinador = convertToEntity(coordinadorDTO);
        coordinador.setCarrera(carrera);
        Coordinador savedCoordinador = coordinadorRepository.save(coordinador);
        inicializarHorarios(savedCoordinador);
        return convertToDto(savedCoordinador);
    }

    @Override
    public void deleteById(Long id) {
        coordinadorRepository.deleteById(id);
    }

    @Override
    public List<HorarioDTO> getDisponibilidad(Long coordinadorId) {
        Coordinador coordinador = coordinadorRepository.findById(coordinadorId)
                .orElseThrow(() -> new ResourceNotFoundException("Coordinador not found with id: " + coordinadorId));
        return coordinador.getHorarios().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private void inicializarHorarios(Coordinador coordinador) {
        List<Horario> horarios = new ArrayList<>();
        for (DayOfWeek dia : DayOfWeek.values()) {
            if (dia == DayOfWeek.SATURDAY || dia == DayOfWeek.SUNDAY) {
                continue; // Excluir sábado y domingo
            }
            for (LocalTime hora = LocalTime.of(9, 0); hora.isBefore(LocalTime.of(12, 0)); hora = hora.plusMinutes(10)) {
                horarios.add(crearHorario(coordinador, dia, hora, hora.plusMinutes(10)));
            }
            for (LocalTime hora = LocalTime.of(13, 0); hora.isBefore(LocalTime.of(16, 0)); hora = hora.plusMinutes(10)) {
                horarios.add(crearHorario(coordinador, dia, hora, hora.plusMinutes(10)));
            }
        }
        horarioRepository.saveAll(horarios);
    }

    private Horario crearHorario(Coordinador coordinador, DayOfWeek dia, LocalTime horaInicio, LocalTime horaFin) {
        Horario horario = new Horario();
        horario.setCoordinador(coordinador);
        horario.setDiaSemana(dia);
        horario.setHoraInicio(horaInicio);
        horario.setHoraFin(horaFin);
        horario.setEstado("libre");
        return horario;
    }

    private CoordinadorDTO convertToDto(Coordinador coordinador) {
        CoordinadorDTO coordinadorDTO = new CoordinadorDTO();
        coordinadorDTO.setId(coordinador.getId());
        coordinadorDTO.setNombre(coordinador.getNombre());
        coordinadorDTO.setEmail(coordinador.getEmail());
        coordinadorDTO.setContrasena(coordinador.getContrasena());
        coordinadorDTO.setCarreraId(coordinador.getCarrera().getId());
        coordinadorDTO.setHorarios(coordinador.getHorarios() != null ? coordinador.getHorarios().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList()) : new ArrayList<>());
        return coordinadorDTO;
    }

    private Coordinador convertToEntity(CoordinadorDTO coordinadorDTO) {
        Coordinador coordinador = new Coordinador();
        coordinador.setId(coordinadorDTO.getId());
        coordinador.setNombre(coordinadorDTO.getNombre());
        coordinador.setEmail(coordinadorDTO.getEmail());
        coordinador.setContrasena(coordinadorDTO.getContrasena());
        coordinador.setHorarios(coordinadorDTO.getHorarios() != null ? coordinadorDTO.getHorarios().stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList()) : new ArrayList<>());
        return coordinador;
    }

    private HorarioDTO convertToDto(Horario horario) {
        HorarioDTO horarioDTO = new HorarioDTO();
        horarioDTO.setId(horario.getId());
        horarioDTO.setHoraInicio(horario.getHoraInicio());
        horarioDTO.setHoraFin(horario.getHoraFin());
        horarioDTO.setDiaSemana(horario.getDiaSemana());
        horarioDTO.setEstado(horario.getEstado());
        return horarioDTO;
    }

    private Horario convertToEntity(HorarioDTO horarioDTO) {
        Horario horario = new Horario();
        horario.setId(horarioDTO.getId());
        horario.setHoraInicio(horarioDTO.getHoraInicio());
        horario.setHoraFin(horarioDTO.getHoraFin());
        horario.setDiaSemana(horarioDTO.getDiaSemana());
        horario.setEstado(horarioDTO.getEstado());
        return horario;
    }
}
