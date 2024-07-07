package org.sample.backendfia.repository;

import org.sample.backendfia.model.Coordinador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CoordinadorRepository extends JpaRepository<Coordinador, Long> {
    Optional<Coordinador> findByEmail(String email);
}
