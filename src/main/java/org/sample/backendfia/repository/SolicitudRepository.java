package org.sample.backendfia.repository;

import org.sample.backendfia.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
    List<Solicitud> findByEstudianteId(Long estudianteId);

    List<Solicitud> findByCoordinadorIdAndFechaAndHora(Long coordinadorId, LocalDate fecha, LocalTime hora);
}
