package com.esports.resultservice.services;

import com.esports.resultservice.models.Resultado;
import com.esports.resultservice.models.dtos.AnulacionDTO;
import com.esports.resultservice.models.dtos.ResultadoDTO;
import java.util.List;

// interfaz que define lo que puede hacer el result-service
public interface ResultadoService {

    List<Resultado> findAll();
    Resultado findById(Long id);
    Resultado findByPartidaId(Long partidaId);
    List<Resultado> findByTorneoId(Long torneoId);
    Resultado save(ResultadoDTO dto);
    Resultado update(Long id, ResultadoDTO dto);
    Resultado validar(Long id);
    Resultado anular(Long id, AnulacionDTO dto);
}
