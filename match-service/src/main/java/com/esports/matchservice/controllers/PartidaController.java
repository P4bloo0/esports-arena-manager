package com.esports.matchservice.controllers;

import com.esports.matchservice.models.Partida;
import com.esports.matchservice.models.dtos.PartidaDTO;
import com.esports.matchservice.services.PartidaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

// guiarse con lo de abajo si no sabe.
@RestController
@RequestMapping("/api/v1/partidas")
@Validated
public class PartidaController {

    @Autowired
    private PartidaService partidaService;

    // GET /api/v1/partidas
    @GetMapping
    public ResponseEntity<List<Partida>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(this.partidaService.findAll());
    }

    // GET /api/v1/partidas/1
    @GetMapping("/{id}")
    public ResponseEntity<Partida> findById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(this.partidaService.findById(id));
    }

    // GET /api/v1/partidas/torneo/1
    @GetMapping("/torneo/{torneoId}")
    public ResponseEntity<List<Partida>> findByTorneo(@PathVariable Long torneoId) {
        return ResponseEntity.status(HttpStatus.OK).body(this.partidaService.findByTorneoId(torneoId));
    }

    // GET /api/v1/partidas/torneo/1/ronda/SEMIFINAL
    @GetMapping("/torneo/{torneoId}/ronda/{ronda}")
    public ResponseEntity<List<Partida>> findByTorneoAndRonda(@PathVariable Long torneoId,
                                                               @PathVariable String ronda) {
        return ResponseEntity.status(HttpStatus.OK).body(this.partidaService.findByTorneoIdAndRonda(torneoId, ronda));
    }

    // GET /api/v1/partidas/estado/PROGRAMADA
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Partida>> findByEstado(@PathVariable String estado) {
        return ResponseEntity.status(HttpStatus.OK).body(this.partidaService.findByEstado(estado));
    }

    // POST /api/v1/partidas
    @PostMapping
    public ResponseEntity<Partida> save(@Valid @RequestBody PartidaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.partidaService.save(dto));
    }

    // PUT /api/v1/partidas/1
    @PutMapping("/{id}")
    public ResponseEntity<Partida> update(@PathVariable Long id, @Valid @RequestBody PartidaDTO dto) {
        return ResponseEntity.ok(this.partidaService.update(id, dto));
    }

    // DELETE /api/v1/partidas/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Partida> cancelar(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(this.partidaService.cancelar(id));
    }
}
