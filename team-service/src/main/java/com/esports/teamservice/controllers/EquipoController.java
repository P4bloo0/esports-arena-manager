package com.esports.teamservice.controllers;

import com.esports.teamservice.models.Equipo;
import com.esports.teamservice.models.MiembroEquipo;
import com.esports.teamservice.models.dtos.EquipoDTO;
import com.esports.teamservice.models.dtos.MiembroEquipoDTO;
import com.esports.teamservice.services.EquipoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/equipos")
@Validated
public class EquipoController {

    @Autowired
    private EquipoService equipoService;

    @GetMapping//GET /api/v1/equipos
    public ResponseEntity<List<Equipo>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(this.equipoService.findAll());
    }

    @GetMapping("/{id}")//GET /api/v1/equipos/1
    public ResponseEntity<Equipo> findById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(this.equipoService.findById(id));
    }

    @GetMapping("/estado/{estado}")//GET /api/v1/equipos/estado/true
    public ResponseEntity<List<Equipo>> findByEstado(@PathVariable Boolean estado) {
        return ResponseEntity.status(HttpStatus.OK).body(this.equipoService.findByEstado(estado));
    }

    @GetMapping("/{id}/miembros")//GET /api/v1/equipos/1/miembros
    public ResponseEntity<List<MiembroEquipo>> findMiembros(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(this.equipoService.findMiembrosByEquipoId(id));
    }


    @PostMapping//POST /api/v1/equipos
    public ResponseEntity<Equipo> save(@Valid @RequestBody EquipoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.equipoService.save(dto));
    }


    @PostMapping("/miembros")//POST /api/v1/equipos/miembros
    public ResponseEntity<MiembroEquipo> agregarMiembro(@Valid @RequestBody MiembroEquipoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.equipoService.agregarMiembro(dto));
    }

    @PutMapping("/{id}")//PUT /api/v1/equipos/1
    public ResponseEntity<Equipo> update(@PathVariable Long id, @Valid @RequestBody EquipoDTO dto) {
        return ResponseEntity.status(HttpStatus.OK).body(this.equipoService.update(id, dto));
    }

    @DeleteMapping("/{id}")//DELETE /api/v1/equipos/1
    public ResponseEntity<Equipo> desactivar(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(this.equipoService.desactivar(id));
    }
}
