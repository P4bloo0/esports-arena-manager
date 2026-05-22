package com.esports.gameservice.controllers;

import com.esports.gameservice.models.Juego;
import com.esports.gameservice.models.dtos.JuegoDTO;
import com.esports.gameservice.services.JuegoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;



@RestController
@RequestMapping("/api/v1/juegos")
public class JuegoController {

    @Autowired
    private JuegoService juegoService;

    //GET /api/v1/juegos
    @GetMapping
    public ResponseEntity<List<Juego>> findAll(){
        return ResponseEntity.ok(juegoService.findAll());
    }

    //GET /api/vs/juegos/activos
    @GetMapping("/activos")
    public ResponseEntity<List<Juego>> findAllActivos(){
        return ResponseEntity.ok(juegoService.findAllActivos());
    }

    //GET /apo/v1/juegos/1
    @GetMapping("/{id}")
    public ResponseEntity<Juego> findById(@PathVariable Long id){
        return ResponseEntity.ok(juegoService.findById(id));
    }

    //POST /api/v1/juegos
    @PostMapping
    public ResponseEntity<Juego> save(@Valid @RequestBody JuegoDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(juegoService.save(dto));

    }

    //PUT /api/v1/juegos/1
    @PutMapping
    public ResponseEntity<Juego> update(@PathVariable Long id, @Valid @RequestBody JuegoDTO dto){
        return ResponseEntity.ok(juegoService.update(id, dto));
    }

    //PUT /api/v1/juegos/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Juego> desactivar(@PathVariable Long id){
        return ResponseEntity.ok(juegoService.desactivar(id));
    }

}
