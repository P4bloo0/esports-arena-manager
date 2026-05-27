package com.esports.sanctionservice.controllers;

import com.esports.sanctionservice.models.Sancion;
import com.esports.sanctionservice.models.dtos.SancionDTO;
import com.esports.sanctionservice.services.SancionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@Validated
@RequestMapping("/api/v1/sanciones")
public class SancionController {

    @Autowired
    private SancionService sancionService;

    // GET /api/v1/sanciones
    @GetMapping
    public ResponseEntity<List<Sancion>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(this.sancionService.findAll());
    }

    // GET /api/v1/sanciones/1
    @GetMapping("/{id}")
    public ResponseEntity<Sancion> findById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(this.sancionService.findById(id));
    }

    //Get /api/v1/sanciones/usuario/1
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Sancion>> findByUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.status(HttpStatus.OK).body(this.sancionService.findByUsuarioId(usuarioId));
    }

    // Get /api/v1/sanciones/equipo/1
    @GetMapping("/equipo/{equipoId}")
    public ResponseEntity<List<Sancion>> findByEquipo(@PathVariable Long equipoId) {
        return ResponseEntity.status(HttpStatus.OK).body(this.sancionService.findByEquipoId(equipoId));
    }

    //Get /api/v1/sanciones
    @GetMapping("/verificar")
    public ResponseEntity<Map<String, Boolean>> verificar(
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) Long equipoId) {
        boolean sancionado = this.sancionService.tieneSancionActiva(usuarioId, equipoId);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("sancionado", sancionado));
    }

    //Post /api/v1/sanciones
    @PostMapping
    public ResponseEntity<Sancion> save(@Valid @RequestBody SancionDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.sancionService.save(dto));
    }

    // Put /api/v1/sanciones/1
    @PutMapping("/{id}")
    public ResponseEntity<Sancion> update(@PathVariable Long id, @Valid @RequestBody SancionDTO dto) {
        return ResponseEntity.status(HttpStatus.OK).body(this.sancionService.update(id, dto));
    }

    //Delete /api/v1/sanciones/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Sancion> cerrar(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(this.sancionService.cerrar(id));
    }




}
