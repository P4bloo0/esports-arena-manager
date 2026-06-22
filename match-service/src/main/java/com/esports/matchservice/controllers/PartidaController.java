package com.esports.matchservice.controllers;

import com.esports.matchservice.models.Partida;
import com.esports.matchservice.models.dtos.PartidaDTO;
import com.esports.matchservice.services.PartidaService;
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
@RequestMapping("/api/v1/partidas")
@Validated
@Tag(name = "Partidas", description = "Metodos CRUD para la gestion de partidas")
public class PartidaController {

    @Autowired
    private PartidaService partidaService;

    @GetMapping
    @Operation(summary = "Listado de todas las partidas", description = "Se devuelve una lista con todas las partidas registradas")
    @ApiResponse(responseCode = "200", description = "Operacion Exitosa")
    public ResponseEntity<List<Partida>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(this.partidaService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar partida por ID", description = "Se devuelve una partida segun su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Partida encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Partida.class))),
            @ApiResponse(responseCode = "400", description = "Partida no encontrada")
    })
    public ResponseEntity<Partida> findById(
            @Parameter(description = "Id de la partida", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(this.partidaService.findById(id));
    }

    @GetMapping("/torneo/{torneoId}")
    @Operation(summary = "Listar partidas por torneo", description = "Devuelve todas las partidas de un torneo")
    @ApiResponse(responseCode = "200", description = "Operacion Exitosa")
    public ResponseEntity<List<Partida>> findByTorneo(
            @Parameter(description = "Id del torneo", required = true, example = "1")
            @PathVariable Long torneoId) {
        return ResponseEntity.status(HttpStatus.OK).body(this.partidaService.findByTorneoId(torneoId));
    }

    @GetMapping("/torneo/{torneoId}/ronda/{ronda}")
    @Operation(summary = "Listar partidas por torneo y ronda", description = "Filtra partidas por torneo y ronda")
    @ApiResponse(responseCode = "200", description = "Operacion Exitosa")
    public ResponseEntity<List<Partida>> findByTorneoAndRonda(
            @Parameter(description = "Id del torneo", required = true, example = "1") @PathVariable Long torneoId,
            @Parameter(description = "Nombre de la ronda", required = true, example = "SEMIFINAL") @PathVariable String ronda) {
        return ResponseEntity.status(HttpStatus.OK).body(this.partidaService.findByTorneoIdAndRonda(torneoId, ronda));
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Listar partidas por estado", description = "Filtra por: PROGRAMADA, EN_CURSO, FINALIZADA, CANCELADA")
    @ApiResponse(responseCode = "200", description = "Operacion Exitosa")
    public ResponseEntity<List<Partida>> findByEstado(
            @Parameter(description = "Estado de la partida", required = true, example = "PROGRAMADA")
            @PathVariable String estado) {
        return ResponseEntity.status(HttpStatus.OK).body(this.partidaService.findByEstado(estado));
    }

    @PostMapping
    @Operation(summary = "Crear una partida", description = "Crea una partida. El torneo debe estar EN_CURSO y ambos participantes inscritos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Partida creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Regla de negocio violada")
    })
    public ResponseEntity<Partida> save(@Valid @RequestBody PartidaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.partidaService.save(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una partida", description = "Actualiza datos de una partida. No se puede modificar si esta CANCELADA o FINALIZADA.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Partida actualizada"),
            @ApiResponse(responseCode = "400", description = "Partida no encontrada o no modificable")
    })
    public ResponseEntity<Partida> update(
            @Parameter(description = "Id de la partida", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody PartidaDTO dto) {
        return ResponseEntity.ok(this.partidaService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancelar una partida", description = "Cancela una partida. No se puede cancelar si ya esta FINALIZADA.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Partida cancelada"),
            @ApiResponse(responseCode = "400", description = "Partida no encontrada o ya finalizada")
    })
    public ResponseEntity<Partida> cancelar(
            @Parameter(description = "Id de la partida a cancelar", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(this.partidaService.cancelar(id));
    }
}