package com.esports.teamservice.services;

import com.esports.teamservice.models.Equipo;
import com.esports.teamservice.models.MiembroEquipo;
import com.esports.teamservice.models.dtos.EquipoDTO;
import com.esports.teamservice.models.dtos.MiembroEquipoDTO;

import java.util.List;

public interface EquipoService {
    List<Equipo> findAll();
    Equipo findById(Long id);
    List<Equipo> findByEstado(Boolean estado);
    Equipo save(EquipoDTO dto);
    Equipo update(Long id, EquipoDTO dto);
    Equipo desactivar(Long id);
    MiembroEquipo agregarMiembro(MiembroEquipoDTO dto);
    List<MiembroEquipo> findMiembrosByEquipoId(Long equipoId);
}
