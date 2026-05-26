package com.esports.rankingservice.services;

import com.esports.rankingservice.models.Ranking;
import com.esports.rankingservice.models.dtos.ActualizarPuntosDTO;
import com.esports.rankingservice.models.dtos.RankingDTO;
import java.util.List;

// interfaz auraaaaaaaa
public interface RankingService {

    List<Ranking> findAll();
    Ranking findById(Long id);
    List<Ranking> findByTorneoId(Long torneoId);
    Ranking findByTorneoIdAndParticipanteId(Long torneoId, Long participanteId);
    Ranking save(RankingDTO dto);
    Ranking actualizarPuntos(Long id, ActualizarPuntosDTO dto);
    List<Ranking> recalcularDesdeResultados(Long torneoId);
    List<Ranking> cerrarRanking(Long torneoId);
}
