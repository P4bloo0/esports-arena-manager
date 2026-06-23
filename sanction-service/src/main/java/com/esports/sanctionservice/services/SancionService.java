package com.esports.sanctionservice.services;

import com.esports.sanctionservice.models.Sancion;
import com.esports.sanctionservice.models.dtos.SancionDTO;
import java.util.List;

// interfaz que permite que puede hacer el servicio
public interface SancionService {

    List<Sancion> findAll();
    Sancion findById(Long id);
    List<Sancion> findByUsuarioId(Long usuarioId);
    List<Sancion> findByEquipoId(Long equipoId);

    Sancion save(SancionDTO dto);
    Sancion update(Long id, SancionDTO dto);
    Sancion cerrar(Long id);

    //llama a registration service para poder saber si se puede inscribir
    boolean tieneSancionActiva(Long usuarioId, Long equipoId);
}
