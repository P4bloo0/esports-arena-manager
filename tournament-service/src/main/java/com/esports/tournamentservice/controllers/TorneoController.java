package com.esports.tournamentservice.controllers;

import com.esports.tournamentservice.models.Torneo;
import com.esports.tournamentservice.models.dtos.TorneoDTO;
import com.esports.tournamentservice.services.TorneoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/torneos")
@Validated
@RestController
public class TorneoController {

    @Autowired
    private TorneoService torneoService;

    @GetMapping //get /api/v1/torneos
    public ResponseEntity<List<Torneo>> findAll(){
        return ResponseEntity.status(HttpStatus.OK).body(this.torneoService.findAll());
    }


    @GetMapping("/{id}") //get /api/v1/torneos/1
    public ResponseEntity<Torneo> findById(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(this.torneoService.findById(id));
    }

    @GetMapping("/estado/{estado}") //get /api/torneos/estado/ABIERTO
    public ResponseEntity<List<Torneo>> findByEstado(@PathVariable String estado){
        return ResponseEntity.status(HttpStatus.OK).body(this.torneoService.findByEstado(estado));
    }

    @GetMapping("/juego/{juegoId}") // get /api/v1/torneos/juego/1
    public ResponseEntity<List<Torneo>> findByJuego(@PathVariable Long juegoId){
        return ResponseEntity.status(HttpStatus.OK).body(this.torneoService.findByJuegoId(juegoId));
    }


    @PostMapping //post /api/v1/torneos
    public ResponseEntity<Torneo> save(@Valid @RequestBody TorneoDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(this.torneoService.save(dto));
    }

    @PutMapping("/{id}")  // put /api/v1/torneos/1
    public ResponseEntity<Torneo> update(@PathVariable Long id, @Valid @RequestBody TorneoDTO dto) {
        return ResponseEntity.status(HttpStatus.OK).body(this.torneoService.update(id, dto));
    }

    @PutMapping("/{id}/estado") // put /api/v1/torneos/1/estado?nuevo = abierto
    public ResponseEntity<Torneo> cambiarEstado(@PathVariable Long id, @RequestParam String nuevo){
        return ResponseEntity.status(HttpStatus.OK).body(this.torneoService.cambiarEstado(id, nuevo));
    }

    @DeleteMapping("/{id}")//delete /api/v1/torneos/1
    public ResponseEntity<Void> delete(@PathVariable Long id){
        this.torneoService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
