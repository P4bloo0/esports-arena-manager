package com.esports.registrationservice.controllers;

import com.esports.registrationservice.models.Inscripcion;
import com.esports.registrationservice.models.dtos.InscripcionDTO;
import com.esports.registrationservice.services.InscripcionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

// soy el chad
@RestController
@RequestMapping("/api/v1/inscripciones")
@Validated
public class InscripcionController {

    @Autowired
    private InscripcionService inscripcionService;

    // GET /api/v1/inscripciones
    @GetMapping
    public ResponseEntity<List<Inscripcion>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(this.inscripcionService.findAll());
    }

    // GET /api/v1/inscripciones/1
    @GetMapping("/{id}")
    public ResponseEntity<Inscripcion> findById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(this.inscripcionService.findById(id));
    }

    // GET /api/v1/inscripciones/torneo/1
    @GetMapping("/torneo/{torneoId}")
    public ResponseEntity<List<Inscripcion>> findByTorneo(@PathVariable Long torneoId) {
        return ResponseEntity.status(HttpStatus.OK).body(this.inscripcionService.findByTorneoId(torneoId));
    }

    // GET /api/v1/inscripciones/equipo/1
    @GetMapping("/equipo/{equipoId}")
    public ResponseEntity<List<Inscripcion>> findByEquipo(@PathVariable Long equipoId) {
        return ResponseEntity.status(HttpStatus.OK).body(this.inscripcionService.findByEquipoId(equipoId));
    }

    // GET /api/v1/inscripciones/jugador/1
    @GetMapping("/jugador/{jugadorId}")
    public ResponseEntity<List<Inscripcion>> findByJugador(@PathVariable Long jugadorId) {
        return ResponseEntity.status(HttpStatus.OK).body(this.inscripcionService.findByJugadorId(jugadorId));
    }

    // POST /api/v1/inscripciones
    @PostMapping
    public ResponseEntity<Inscripcion> save(@Valid @RequestBody InscripcionDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.inscripcionService.save(dto));
    }

    // PUT /api/v1/inscripciones/1/estado?nuevoEstado=CONFIRMADA
    @PutMapping("/{id}/estado")
    public ResponseEntity<Inscripcion> actualizarEstado(@PathVariable Long id,
                                                         @RequestParam String nuevoEstado) {
        return ResponseEntity.ok(this.inscripcionService.actualizarEstado(id, nuevoEstado));
    }

    // DELETE /api/v1/inscripciones/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Inscripcion> cancelar(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(this.inscripcionService.cancelar(id));
    }
}
