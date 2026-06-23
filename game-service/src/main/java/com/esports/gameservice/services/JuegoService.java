package com.esports.gameservice.services;

import com.esports.gameservice.models.Juego;
import com.esports.gameservice.models.dtos.JuegoDTO;
import java.util.List;


public interface JuegoService {
    List<Juego> findAll();
    List<Juego> findAllActivos();
    Juego findById(Long id);
    Juego save(JuegoDTO dto);
    Juego update(Long id, JuegoDTO dto);
    Juego desactivar(Long id);
}