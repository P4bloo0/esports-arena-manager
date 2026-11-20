package com.esports.tournamentservice.repositories;

import com.esports.tournamentservice.models.Torneo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TorneoRepository extends JpaRepository<Torneo, Long> {
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select t from Torneo t where t.torneoId=:id") java.util.Optional<Torneo> lock(Long id);

    List<Torneo> findByEstado(String estado);

    List<Torneo> findByJuegoId(Long juegoId);
}
