package com.esports.registrationservice.repositories;

import com.esports.registrationservice.models.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;


@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {


    List<Inscripcion> findByTorneoId(Long torneoId);


    List<Inscripcion> findByEquipoId(Long equipoId);


    List<Inscripcion> findByJugadorId(Long jugadorId);


    Optional<Inscripcion> findByTorneoIdAndEquipoId(Long torneoId, Long equipoId);


    Optional<Inscripcion> findByTorneoIdAndJugadorId(Long torneoId, Long jugadorId);


    long countByTorneoIdAndEstado(Long torneoId, Inscripcion.Estado estado);
}
