package com.esports.teamservice.security;
import com.esports.common.*;
import com.esports.teamservice.repositories.*;
import org.springframework.stereotype.Component;
@Component("equipoAccess")
public class EquipoAccess {
 private final EquipoRepository equipos;
 public EquipoAccess(EquipoRepository equipos) { this.equipos=equipos; }
 public boolean manage(Long id) { if(Actor.staff()) return true; return equipos.findById(id).map(e -> e.getCapitanId().equals(Actor.id())).orElseThrow(() -> ArenaException.missing("Equipo no encontrado")); }
 public boolean create(Long capitan) { return Actor.staff() || capitan.equals(Actor.id()); }
}
