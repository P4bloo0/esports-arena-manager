package com.esports.registrationservice.security;
import com.esports.registrationservice.clients.EquipoClient;
import com.esports.registrationservice.models.*;
import com.esports.registrationservice.models.dtos.InscripcionDTO;
import com.esports.registrationservice.repositories.InscripcionRepository;
import com.esports.common.*;
import org.springframework.stereotype.Component;
@Component("inscripcionAccess")
public class InscripcionAccess {
 private final EquipoClient equipos; private final InscripcionRepository repo;
 public InscripcionAccess(EquipoClient equipos,InscripcionRepository repo) { this.equipos=equipos; this.repo=repo; }
 public boolean team(Long id) { if(Actor.staff()) return true; return equipos.getEquipoById(id).getCapitanId().equals(Actor.id()) || equipos.miembros(id).stream().anyMatch(m -> m.usuarioId().equals(Actor.id())); }
 public boolean submit(InscripcionDTO dto) { return Actor.staff() || (dto.getTipoParticipante()==Inscripcion.TipoParticipante.INDIVIDUAL ? Actor.id().equals(dto.getJugadorId()) : dto.getEquipoId()!=null && team(dto.getEquipoId())); }
 public boolean manage(Long id) { var i=repo.findById(id).orElseThrow(() -> ArenaException.missing("Inscripcion no encontrada")); return Actor.staff() || (i.getEquipoId()!=null?team(i.getEquipoId()):Actor.id().equals(i.getJugadorId())); }
}
