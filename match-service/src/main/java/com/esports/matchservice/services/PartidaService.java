package com.esports.matchservice.services;

import com.esports.matchservice.models.Partida;
import com.esports.matchservice.models.dtos.PartidaDTO;
import java.util.List;

// interfaz que define lo que puede hacer el match-service
// la logica real esta en PartidaServiceImpl
public interface PartidaService {

    List<Partida> findAll();
    Partida findById(Long id);
    List<Partida> findByTorneoId(Long torneoId);
    List<Partida> findByTorneoIdAndRonda(Long torneoId, String ronda);
    List<Partida> findByEstado(String estado);
    Partida save(PartidaDTO dto);
    Partida update(Long id, PartidaDTO dto);
    Partida cancelar(Long id);
}
