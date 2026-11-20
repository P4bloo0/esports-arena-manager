package com.esports.teamservice.security;

import com.esports.common.Actor;
import com.esports.common.ArenaException;
import com.esports.teamservice.repositories.EquipoRepository;
import org.springframework.stereotype.Component;

@Component("equipoAccess")
public class EquipoAccess {
 private final EquipoRepository equipos;

 public EquipoAccess(EquipoRepository equipos) {
  this.equipos = equipos;
 }

 public boolean manage(Long id) {
  if (Actor.admin()) return true;
  return equipos.findById(id)
          .map(e -> Actor.id().equals(e.getCapitanId()))
          .orElseThrow(() -> ArenaException.missing("Equipo no encontrado"));
 }

 public boolean create(Long capitanId) {
  return Actor.admin() || Actor.id().equals(capitanId);
 }
}