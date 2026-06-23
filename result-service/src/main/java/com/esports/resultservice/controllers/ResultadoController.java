package com.esports.resultservice.controllers;

import com.esports.resultservice.models.Resultado;
import com.esports.resultservice.models.dtos.AnulacionDTO;
import com.esports.resultservice.models.dtos.ResultadoDTO;
import com.esports.resultservice.services.ResultadoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/v1/resultados")
@Validated
public class ResultadoController {

    @Autowired
    private ResultadoService resultadoService;

    // GET /api/v1/resultados
    @GetMapping
    public ResponseEntity<List<Resultado>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(this.resultadoService.findAll());
    }

    // get /api/v1/resultados/1


    @GetMapping("/{id}")
    public ResponseEntity<Resultado> findById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(this.resultadoService.findById(id));
    }

    // GET /api/v1/resultados/partida/1
    @GetMapping("/partida/{partidaId}")
    public ResponseEntity<Resultado> findByPartida(@PathVariable Long partidaId) {
        return ResponseEntity.status(HttpStatus.OK).body(this.resultadoService.findByPartidaId(partidaId));
    }

    // GET /api/v1/resultados/torneo/1
    @GetMapping("/torneo/{torneoId}")
    public ResponseEntity<List<Resultado>> findByTorneo(@PathVariable Long torneoId) {
        return ResponseEntity.status(HttpStatus.OK).body(this.resultadoService.findByTorneoId(torneoId));
    }

    // POST /api/v1/resultados
    @PostMapping
    public ResponseEntity<Resultado> save(@Valid @RequestBody ResultadoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.resultadoService.save(dto));
    }

    // PUT /api/v1/resultados/1
    @PutMapping("/{id}")
    public ResponseEntity<Resultado> update(@PathVariable Long id, @Valid @RequestBody ResultadoDTO dto) {
        return ResponseEntity.ok(this.resultadoService.update(id, dto));
    }

    // PATCH /api/v1/resultados/1/validar
    @PatchMapping("/{id}/validar")
    public ResponseEntity<Resultado> validar(@PathVariable Long id) {
        return ResponseEntity.ok(this.resultadoService.validar(id));
    }

    // PATCH /api/v1/resultados/1/anular
    @PatchMapping("/{id}/anular")
    public ResponseEntity<Resultado> anular(@PathVariable Long id, @Valid @RequestBody AnulacionDTO dto) {
        return ResponseEntity.ok(this.resultadoService.anular(id, dto));
    }
}
