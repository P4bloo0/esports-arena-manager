package com.esports.matchservice.controllers;
import com.esports.matchservice.models.Partida;
import com.esports.matchservice.repositories.PartidaRepository;
import com.esports.common.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
@RestController
public class EstadoPartidaController {
 private final PartidaRepository repo; public EstadoPartidaController(PartidaRepository repo) { this.repo=repo; }
 @PatchMapping("/api/v1/partidas/{id}/estado") @Transactional public Partida state(@PathVariable Long id,@RequestParam Partida.Estado nuevo) {
  var p=repo.findById(id).orElseThrow(() -> ArenaException.missing("Partida no encontrada"));
  if(!(p.getEstado()==Partida.Estado.PROGRAMADA && nuevo==Partida.Estado.EN_CURSO || p.getEstado()==Partida.Estado.EN_CURSO && nuevo==Partida.Estado.FINALIZADA)) throw ArenaException.conflict("Transicion de partida no permitida");
  p.setEstado(nuevo); return repo.save(p);
 }
}
