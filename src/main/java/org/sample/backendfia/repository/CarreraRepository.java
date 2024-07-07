package org.sample.backendfia.repository;

import org.sample.backendfia.model.Carrera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarreraRepository extends JpaRepository<Carrera, Long> {
    Optional<Carrera> findByNombre(String nombre);
}
