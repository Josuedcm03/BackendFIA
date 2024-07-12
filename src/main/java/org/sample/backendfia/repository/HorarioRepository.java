package org.sample.backendfia.repository;

import org.sample.backendfia.model.Horario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface HorarioRepository extends JpaRepository<Horario, Long> {
    //  List<Horario> findByCoordinadorIdAndDiaSemanaAndHoraInicio(Long coordinadorId, DayOfWeek d1iaSemana, LocalTime horaInicio);
    List<Horario> findByCoordinadorIdAndFechaAndHoraInicio(Long coordinadorId, LocalDate fecha, LocalTime horaInicio);

}
