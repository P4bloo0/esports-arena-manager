package com.esports.teamservice.controllers;
import com.esports.teamservice.models.*;
import com.esports.teamservice.repositories.*;
import com.esports.teamservice.services.EquipoService;
import com.esports.common.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@RestController @RequestMapping("/api/v1/equipos")
public class PlantillaController {
 private final EquipoService equipos; private final MiembroEquipoRepository miembros;
 public PlantillaController(EquipoService equipos,MiembroEquipoRepository miembros) { this.equipos=equipos; this.miembros=miembros; }
 @GetMapping("/mis") @PreAuthorize("isAuthenticated()") public List<Equipo> mine() { var id=Actor.id(); var ids=miembros.findByUsuarioId(id).stream().map(MiembroEquipo::getEquipoId).toList(); return equipos.findAll().stream().filter(e -> e.getCapitanId().equals(id)||ids.contains(e.getEquipoId())).toList(); }
 @DeleteMapping("/{id}/miembros/{usuarioId}") @PreAuthorize("@equipoAccess.manage(#id)") @Transactional
 public void remove(@PathVariable Long id,@PathVariable Long usuarioId) {
  if(equipos.findById(id).getCapitanId().equals(usuarioId)) throw ArenaException.conflict("Primero debes cambiar el capitan.");
  miembros.delete(miembros.findByEquipoIdAndUsuarioId(id,usuarioId).orElseThrow(() -> ArenaException.missing("Integrante no encontrado")));
 }
}
