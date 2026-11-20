package com.esports.resultservice.security;
import com.esports.resultservice.clients.*;
import com.esports.resultservice.repositories.ResultadoRepository;
import com.esports.common.*;
import org.springframework.stereotype.Component;
@Component("resultadoAccess") public class ResultadoAccess {
 private final PartidaClient partidas; private final TorneoClient torneos; private final ResultadoRepository resultados;
 public ResultadoAccess(PartidaClient partidas,TorneoClient torneos,ResultadoRepository resultados) { this.partidas=partidas; this.torneos=torneos; this.resultados=resultados; }
 public boolean partida(Long id) { if("FINALIZADO".equals(torneos.buscar(partidas.getPartidaById(id).getTorneoId()).estado())) throw ArenaException.conflict("El torneo esta finalizado"); return true; }
 public boolean resultado(Long id) { return partida(resultados.findById(id).orElseThrow(() -> ArenaException.missing("Resultado no encontrado")).getPartidaId()); }
}
