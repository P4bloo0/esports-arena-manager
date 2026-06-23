package com.esports.sanctionservice.controllers;

import com.esports.sanctionservice.models.Sancion;
import com.esports.sanctionservice.models.dtos.SancionDTO;
import com.esports.sanctionservice.services.SancionService;
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
import java.util.Map;

@RestController
@Validated
@RequestMapping("/api/v1/sanciones")
@Tag(name = "Sanciones", description = "Metodos CRUD para la gestion de sanciones a usuarios y equipos")
public class SancionController {

    @Autowired
    private SancionService sancionService;

    @GetMapping
    @Operation(summary = "Listar todas las sanciones", description = "Devuelve todas las sanciones registradas en el sistema")
    @ApiResponse(responseCode = "200", description = "Operacion exitosa")
    public ResponseEntity<List<Sancion>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(this.sancionService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar sancion por ID", description = "Devuelve una sancion segun su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sancion encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Sancion.class))),
            @ApiResponse(responseCode = "400", description = "Sancion no encontrada")
    })
    public ResponseEntity<Sancion> findById(
            @Parameter(description = "Id de la sancion", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(this.sancionService.findById(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Listar sanciones por usuario", description = "Devuelve todas las sanciones de un usuario especifico")
    @ApiResponse(responseCode = "200", description = "Operacion exitosa")
    public ResponseEntity<List<Sancion>> findByUsuario(
            @Parameter(description = "Id del usuario", required = true, example = "1")
            @PathVariable Long usuarioId) {
        return ResponseEntity.status(HttpStatus.OK).body(this.sancionService.findByUsuarioId(usuarioId));
    }

    @GetMapping("/equipo/{equipoId}")
    @Operation(summary = "Listar sanciones por equipo", description = "Devuelve todas las sanciones de un equipo especifico")
    @ApiResponse(responseCode = "200", description = "Operacion exitosa")
    public ResponseEntity<List<Sancion>> findByEquipo(
            @Parameter(description = "Id del equipo", required = true, example = "1")
            @PathVariable Long equipoId) {
        return ResponseEntity.status(HttpStatus.OK).body(this.sancionService.findByEquipoId(equipoId));
    }

    @GetMapping("/verificar")
    @Operation(summary = "Verificar sancion activa", description = "Verifica si un usuario o equipo tiene una sancion activa. Retorna {sancionado: true/false}")
    @ApiResponse(responseCode = "200", description = "Resultado de verificacion")
    public ResponseEntity<Map<String, Boolean>> verificar(
            @Parameter(description = "Id del usuario a verificar", example = "1")
            @RequestParam(required = false) Long usuarioId,
            @Parameter(description = "Id del equipo a verificar", example = "1")
            @RequestParam(required = false) Long equipoId) {
        boolean sancionado = this.sancionService.tieneSancionActiva(usuarioId, equipoId);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("sancionado", sancionado));
    }

    @PostMapping
    @Operation(summary = "Crear una sancion", description = "Registra una nueva sancion. Debe indicar al menos usuario o equipo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sancion creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos o sin destinatario")
    })
    public ResponseEntity<Sancion> save(@Valid @RequestBody SancionDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.sancionService.save(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una sancion", description = "Actualiza los datos de una sancion que no este cerrada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sancion actualizada"),
            @ApiResponse(responseCode = "400", description = "Sancion cerrada o no encontrada")
    })
    public ResponseEntity<Sancion> update(
            @Parameter(description = "Id de la sancion a actualizar", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody SancionDTO dto) {
        return ResponseEntity.status(HttpStatus.OK).body(this.sancionService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cerrar una sancion", description = "Cambia el estado de la sancion a CERRADA")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sancion cerrada"),
            @ApiResponse(responseCode = "400", description = "Sancion no encontrada")
    })
    public ResponseEntity<Sancion> cerrar(
            @Parameter(description = "Id de la sancion a cerrar", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(this.sancionService.cerrar(id));
    }
}
