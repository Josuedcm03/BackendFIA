package org.sample.backendfia.repository;

import org.sample.backendfia.model.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {
    Optional<Estudiante> findByCifAndContrasena(String cif, String contrasena);
    Optional<Estudiante> findByCif(String cif);
}
