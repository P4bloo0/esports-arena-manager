package com.esports.tournamentservice.controllers;

import com.esports.tournamentservice.models.Torneo;
import com.esports.tournamentservice.models.dtos.TorneoDTO;
import com.esports.tournamentservice.services.TorneoService;
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
@RequestMapping("/api/v1/torneos")
@Validated
@Tag(name = "Torneos", description = "Metodos CRUD para la gestion de torneos")
public class TorneoController {

    @Autowired
    private TorneoService torneoService;

    @GetMapping
    @Operation(summary = "Listado de todos los torneos", description = "Se devuelve una lista con todos los torneos registrados")
    @ApiResponse(responseCode = "200", description = "Operacion Exitosa")
    public ResponseEntity<List<Torneo>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(this.torneoService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar torneo por ID", description = "Se devuelve un torneo segun su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Torneo encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Torneo.class))),
            @ApiResponse(responseCode = "400", description = "Torneo no encontrado")
    })
    public ResponseEntity<Torneo> findById(
            @Parameter(description = "Id del torneo a buscar", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(this.torneoService.findById(id));
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Listar torneos por estado", description = "Filtra torneos por estado: BORRADOR, ABIERTO, EN_CURSO, FINALIZADO")
    @ApiResponse(responseCode = "200", description = "Operacion Exitosa")
    public ResponseEntity<List<Torneo>> findByEstado(
            @Parameter(description = "Estado del torneo", required = true, example = "ABIERTO")
            @PathVariable String estado) {
        return ResponseEntity.status(HttpStatus.OK).body(this.torneoService.findByEstado(estado));
    }

    @GetMapping("/juego/{juegoId}")
    @Operation(summary = "Listar torneos por juego", description = "Devuelve los torneos asociados a un juego")
    @ApiResponse(responseCode = "200", description = "Operacion Exitosa")
    public ResponseEntity<List<Torneo>> findByJuego(
            @Parameter(description = "Id del juego", required = true, example = "1")
            @PathVariable Long juegoId) {
        return ResponseEntity.status(HttpStatus.OK).body(this.torneoService.findByJuegoId(juegoId));
    }

    @PostMapping
    @Operation(summary = "Crear un torneo", description = "Registra un nuevo torneo. El juego debe existir en game-service.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Torneo creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos o juego no existe")
    })
    public ResponseEntity<Torneo> save(@Valid @RequestBody TorneoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.torneoService.save(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un torneo", description = "Actualiza los datos de un torneo. No se puede modificar si esta EN_CURSO.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Torneo actualizado"),
            @ApiResponse(responseCode = "400", description = "Torneo no encontrado o en curso")
    })
    public ResponseEntity<Torneo> update(
            @Parameter(description = "Id del torneo a actualizar", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody TorneoDTO dto) {
        return ResponseEntity.status(HttpStatus.OK).body(this.torneoService.update(id, dto));
    }

    @PutMapping("/{id}/estado")
    @Operation(summary = "Cambiar estado del torneo", description = "Cambia el estado: BORRADOR, ABIERTO, EN_CURSO, FINALIZADO")
    @ApiResponse(responseCode = "200", description = "Estado actualizado")
    public ResponseEntity<Torneo> cambiarEstado(
            @Parameter(description = "Id del torneo", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Nuevo estado", required = true, example = "ABIERTO")
            @RequestParam String nuevo) {
        return ResponseEntity.status(HttpStatus.OK).body(this.torneoService.cambiarEstado(id, nuevo));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un torneo", description = "Elimina un torneo por su id")
    @ApiResponse(responseCode = "204", description = "Torneo eliminado")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Id del torneo a eliminar", required = true, example = "1")
            @PathVariable Long id) {
        this.torneoService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}