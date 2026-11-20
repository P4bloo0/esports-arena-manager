package com.esports.tournamentservice.services;
import com.esports.tournamentservice.models.*;
import com.esports.tournamentservice.repositories.*;
import com.esports.tournamentservice.clients.*;
import com.esports.common.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Service
public class CierreTorneo {
 private final TorneoRepository torneos; private final PremioRepository premios; private final EntregaRepository entregas;
 private final CierreRankingClient rankings; private final CierrePartidasClient partidas; private final CierreResultadosClient resultados; private final CierreEquipoClient equipos;
 public CierreTorneo(TorneoRepository torneos,PremioRepository premios,EntregaRepository entregas,CierreRankingClient rankings,CierrePartidasClient partidas,CierreResultadosClient resultados,CierreEquipoClient equipos) { this.torneos=torneos; this.premios=premios; this.entregas=entregas; this.rankings=rankings; this.partidas=partidas; this.resultados=resultados; this.equipos=equipos; }
 @Transactional public Torneo finalizar(Long id) {
  var t=torneos.lock(id).orElseThrow(() -> ArenaException.missing("Torneo no encontrado"));
  if("FINALIZADO".equals(t.getEstado())) return t;
  if(!"EN_CURSO".equals(t.getEstado())) throw ArenaException.conflict("Solo se puede finalizar un torneo en curso");
  var cruces=partidas.listar(id);
  if(cruces.isEmpty() || cruces.stream().anyMatch(p -> !Set.of("FINALIZADA","CANCELADA").contains(p.estado()))) throw ArenaException.conflict("Finaliza o cancela las partidas antes de cerrar el torneo");
  var validos=resultados.listar(id).stream().filter(r -> "VALIDADO".equals(r.estadoValidacion())).map(CierreResultadosClient.Resultado::partidaId).collect(java.util.stream.Collectors.toSet());
  if(cruces.stream().filter(p -> "FINALIZADA".equals(p.estado())).anyMatch(p -> !validos.contains(p.partidaId()))) throw ArenaException.conflict("Hay partidas sin resultado validado");
  var tabla=rankings.recalcular(id);
  boolean individual=t.getModalidad().toLowerCase().matches(".*(solo|individual|1\\s*(vs|v)\\s*1).*");
  for(var premio:premios.findByTorneoIdOrderByPosicionAsc(id)) {
   var posicion=tabla.stream().filter(r -> r.posicion().equals(premio.getPosicion())).findFirst().orElseThrow(() -> ArenaException.conflict("No hay participante para el premio de posicion "+premio.getPosicion()));
   premio.setParticipanteId(posicion.participanteId()); premio.setTipoParticipante(individual?"INDIVIDUAL":"EQUIPO"); premios.save(premio);
   List<Long> destinatarios=individual?List.of(posicion.participanteId()):equipos.miembros(posicion.participanteId()).stream().map(CierreEquipoClient.Miembro::usuarioId).distinct().toList();
   for(var usuario:destinatarios) {
    var entrega=new EntregaPendiente(); entrega.setId("PREMIO:"+id+":"+premio.getPosicion()+":"+usuario); entrega.setUsuarioId(usuario); entrega.setMensaje("Obtuviste el premio de posicion "+premio.getPosicion()+" en "+t.getNombre()+": "+premio.getDescripcion()); entregas.save(entrega);
   }
  }
  t.setEstado("FINALIZADO"); return torneos.save(t);
 }
}
