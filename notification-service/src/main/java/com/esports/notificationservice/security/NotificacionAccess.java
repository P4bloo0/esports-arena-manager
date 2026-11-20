package com.esports.notificationservice.security;
import com.esports.notificationservice.repositories.NotificacionRepository;
import com.esports.notificationservice.clients.EquipoClient;
import com.esports.common.*;
import org.springframework.stereotype.Component;
@Component("notificacionAccess") public class NotificacionAccess {
 private final NotificacionRepository repo; private final EquipoClient equipos;
 public NotificacionAccess(NotificacionRepository repo,EquipoClient equipos) { this.repo=repo; this.equipos=equipos; }
 public boolean team(Long id) { return Actor.staff() || equipos.miembros(id).stream().anyMatch(m -> m.usuarioId().equals(Actor.id())); }
 public boolean read(Long id) { var n=repo.findById(id).orElseThrow(() -> ArenaException.missing("Notificacion no encontrada")); return Actor.staff() || n.getUsuarioId()!=null && n.getUsuarioId().equals(Actor.id()) || n.getEquipoId()!=null && team(n.getEquipoId()); }
}
