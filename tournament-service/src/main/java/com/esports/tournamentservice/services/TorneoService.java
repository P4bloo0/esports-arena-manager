package com.esports.tournamentservice.services;

import com.esports.tournamentservice.models.Torneo;
import com.esports.tournamentservice.models.dtos.TorneoDTO;
import java.util.List;

public interface TorneoService {

    List<Torneo> findAll();
    Torneo findById(Long id);
    List<Torneo> findByEstado(String estado);
    List<Torneo> findByJuegoId(Long juegoId);
    Torneo save(TorneoDTO dto);
    Torneo update(Long id, TorneoDTO dto);
    Torneo cambiarEstado(Long id, String nuevoEstado);
    void deleteById(Long id);

}
