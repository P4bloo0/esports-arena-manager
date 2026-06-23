package com.esports.rankingservice.repositories;

import com.esports.rankingservice.models.Ranking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

// habla con la base de datos sisplau
@Repository
public interface RankingRepository extends JpaRepository<Ranking, Long> {


    List<Ranking> findByTorneoIdOrderByPuntosDescDiferenciaDesc(Long torneoId);

    Optional<Ranking> findByTorneoIdAndParticipanteId(Long torneoId, Long participanteId);



    List<Ranking> findByTorneoIdOrderByPuntosDesc(Long torneoId);
}
