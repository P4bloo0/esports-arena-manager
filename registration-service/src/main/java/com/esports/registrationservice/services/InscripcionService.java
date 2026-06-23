package com.esports.registrationservice.services;

import com.esports.registrationservice.models.Inscripcion;
import com.esports.registrationservice.models.dtos.InscripcionDTO;
import java.util.List;

// interfaz que define lo que puede hacer el registration-service
// la logica real esta en InscripcionServiceImpl
public interface InscripcionService {

    List<Inscripcion> findAll();
    Inscripcion findById(Long id);
    List<Inscripcion> findByTorneoId(Long torneoId);
    List<Inscripcion> findByEquipoId(Long equipoId);
    List<Inscripcion> findByJugadorId(Long jugadorId);
    Inscripcion save(InscripcionDTO dto);
    Inscripcion actualizarEstado(Long id, String nuevoEstado);
    Inscripcion cancelar(Long id);
}
