package com.esports.tournamentservice.services;
import org.springframework.stereotype.Service;
import com.esports.tournamentservice.models.Torneo;
import com.esports.tournamentservice.clients.InscripcionesClient;
import java.util.List;
@Service public class TorneoView {
 private final InscripcionesClient inscripciones; public TorneoView(InscripcionesClient inscripciones) { this.inscripciones=inscripciones; }
 public Torneo completar(Torneo torneo) { torneo.setCupoActual(Math.toIntExact(inscripciones.ocupados(torneo.getTorneoId()))); return torneo; }
 public List<Torneo> completar(List<Torneo> lista) { return lista.stream().map(this::completar).toList(); }
}
