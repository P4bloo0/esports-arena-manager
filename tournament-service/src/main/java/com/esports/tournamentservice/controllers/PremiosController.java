package com.esports.tournamentservice.controllers;
import com.esports.tournamentservice.models.*;
import com.esports.tournamentservice.repositories.*;
import com.esports.common.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;
@RestController @RequestMapping("/api/v1/torneos/{id}/premios")
public class PremiosController {
 private final PremioRepository premios; private final TorneoRepository torneos;
 public PremiosController(PremioRepository premios,TorneoRepository torneos) { this.premios=premios; this.torneos=torneos; }
 public record Solicitud(@Min(1) int posicion,@NotBlank @Size(max=500) String descripcion) {}
 @GetMapping public List<Premio> publicos(@PathVariable Long id) { var t=torneos.findById(id).orElseThrow(() -> ArenaException.missing("Torneo no encontrado")); return "FINALIZADO".equals(t.getEstado())?premios.findByTorneoIdOrderByPosicionAsc(id):List.of(); }
 @GetMapping("/configuracion") @PreAuthorize("hasAuthority('ADMINISTRADOR')") public List<Premio> configuracion(@PathVariable Long id) { return premios.findByTorneoIdOrderByPosicionAsc(id); }
 @PostMapping @Transactional public Premio guardar(@PathVariable Long id,@Valid @RequestBody Solicitud dto) {
  var t=torneos.lock(id).orElseThrow(() -> ArenaException.missing("Torneo no encontrado"));
  if(!java.util.Set.of("BORRADOR","ABIERTO").contains(t.getEstado())) throw ArenaException.conflict("Los premios se configuran antes de iniciar el torneo");
  var p=premios.findByTorneoIdAndPosicion(id,dto.posicion()).orElseGet(Premio::new); p.setTorneoId(id); p.setPosicion(dto.posicion()); p.setDescripcion(dto.descripcion().trim()); return premios.save(p);
 }
 @DeleteMapping("/{posicion}") @Transactional public void quitar(@PathVariable Long id,@PathVariable Integer posicion) { var t=torneos.lock(id).orElseThrow(); if(!java.util.Set.of("BORRADOR","ABIERTO").contains(t.getEstado())) throw ArenaException.conflict("El torneo ya inicio"); premios.findByTorneoIdAndPosicion(id,posicion).ifPresent(premios::delete); }
}
