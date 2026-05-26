package com.esports.rankingservice.controllers;

import com.esports.rankingservice.models.Ranking;
import com.esports.rankingservice.models.dtos.ActualizarPuntosDTO;
import com.esports.rankingservice.models.dtos.RankingDTO;
import com.esports.rankingservice.services.RankingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

// soy el chad
@RestController
@RequestMapping("/api/v1/rankings")
@Validated
public class RankingController {

    @Autowired
    private RankingService rankingService;

    // GET /api/v1/rankings
    @GetMapping
    public ResponseEntity<List<Ranking>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(this.rankingService.findAll());
    }

    // GET /api/v1/rankings/1
    @GetMapping("/{id}")
    public ResponseEntity<Ranking> findById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(this.rankingService.findById(id));
    }

    // GET /api/v1/rankings/torneo/1
    @GetMapping("/torneo/{torneoId}")
    public ResponseEntity<List<Ranking>> findByTorneo(@PathVariable Long torneoId) {
        return ResponseEntity.status(HttpStatus.OK).body(this.rankingService.findByTorneoId(torneoId));
    }

    // GET /api/v1/rankings/torneo/1/participante/2
    @GetMapping("/torneo/{torneoId}/participante/{participanteId}")
    public ResponseEntity<Ranking> findByTorneoAndParticipante(@PathVariable Long torneoId,
                                                                @PathVariable Long participanteId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(this.rankingService.findByTorneoIdAndParticipanteId(torneoId, participanteId));
    }

    // POST /api/v1/rankings
    @PostMapping
    public ResponseEntity<Ranking> save(@Valid @RequestBody RankingDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.rankingService.save(dto));
    }

    // PUT /api/v1/rankings/1/puntos
    @PutMapping("/{id}/puntos")
    public ResponseEntity<Ranking> actualizarPuntos(@PathVariable Long id,
                                                     @Valid @RequestBody ActualizarPuntosDTO dto) {
        return ResponseEntity.ok(this.rankingService.actualizarPuntos(id, dto));
    }

    // POST /api/v1/rankings/torneo/1/recalcular
    @PostMapping("/torneo/{torneoId}/recalcular")
    public ResponseEntity<List<Ranking>> recalcular(@PathVariable Long torneoId) {
        return ResponseEntity.ok(this.rankingService.recalcularDesdeResultados(torneoId));
    }

    // PATCH /api/v1/rankings/torneo/1/cerrar
    @PatchMapping("/torneo/{torneoId}/cerrar")
    public ResponseEntity<List<Ranking>> cerrar(@PathVariable Long torneoId) {
        return ResponseEntity.ok(this.rankingService.cerrarRanking(torneoId));
    }
}
