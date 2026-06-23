package com.esports.matchservice.repositories;

import com.esports.matchservice.models.Partida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;


@Repository
public interface PartidaRepository extends JpaRepository<Partida, Long> {


    List<Partida> findByTorneoId(Long torneoId);


    List<Partida> findByTorneoIdAndRonda(Long torneoId, String ronda);


    List<Partida> findByEstado(Partida.Estado estado);


    Optional<Partida> findByTorneoIdAndParticipanteAIdAndParticipanteBIdAndRonda(
            Long torneoId, Long participanteAId, Long participanteBId, String ronda);
}
