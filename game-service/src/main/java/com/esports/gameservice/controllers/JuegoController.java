package com.esports.gameservice.controllers;

import com.esports.gameservice.models.Juego;
import com.esports.gameservice.models.dtos.JuegoDTO;
import com.esports.gameservice.services.JuegoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/juegos")
@Validated
@Tag(name = "Juegos", description = "Metodos CRUD para la gestion de juegos")
public class JuegoController {

    @Autowired
    private JuegoService juegoService;

    @GetMapping
    @Operation(summary = "Listar todos los juegos", description = "Devuelve todos los juegos registrados en el sistema")
    @ApiResponse(responseCode = "200", description = "Operacion exitosa")
    public ResponseEntity<List<Juego>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(this.juegoService.findAll());
    }

    @GetMapping("/activos")
    @Operation(summary = "Listar juegos activos", description = "Devuelve solo los juegos con estado activo")
    @ApiResponse(responseCode = "200", description = "Operacion exitosa")
    public ResponseEntity<List<Juego>> findAllActivos() {
        return ResponseEntity.status(HttpStatus.OK).body(this.juegoService.findAllActivos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar juego por ID", description = "Devuelve un juego segun su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Juego encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Juego.class))),
            @ApiResponse(responseCode = "400", description = "Juego no encontrado")
    })
    public ResponseEntity<Juego> findById(
            @Parameter(description = "Id del juego a buscar", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(this.juegoService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Crear un juego", description = "Registra un nuevo juego. El nombre debe ser unico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Juego creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos o nombre duplicado")
    })
    public ResponseEntity<Juego> save(@Valid @RequestBody JuegoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(juegoService.save(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un juego", description = "Actualiza los datos de un juego existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Juego actualizado"),
            @ApiResponse(responseCode = "400", description = "Juego no encontrado")
    })
    public ResponseEntity<Juego> update(
            @Parameter(description = "Id del juego a actualizar", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody JuegoDTO dto) {
        return ResponseEntity.status(HttpStatus.OK).body(this.juegoService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar un juego", description = "Cambia el estado del juego a inactivo (false)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Juego desactivado"),
            @ApiResponse(responseCode = "400", description = "Juego no encontrado")
    })
    public ResponseEntity<Juego> desactivar(
            @Parameter(description = "Id del juego a desactivar", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(this.juegoService.desactivar(id));
    }
}
