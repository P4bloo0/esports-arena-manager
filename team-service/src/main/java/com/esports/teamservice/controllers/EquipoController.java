package com.esports.teamservice.controllers;

import com.esports.teamservice.models.Equipo;
import com.esports.teamservice.models.MiembroEquipo;
import com.esports.teamservice.models.dtos.EquipoDTO;
import com.esports.teamservice.models.dtos.MiembroEquipoDTO;
import com.esports.teamservice.services.EquipoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Equipos", description = "Metodos CRUD para la gestion de equipos y miembros")
public class EquipoController {

    @Autowired
    private EquipoService equipoService;

    @GetMapping
    @Operation(summary = "Listar todos los equipos", description = "Devuelve todos los equipos registrados")
    @ApiResponse(responseCode = "200", description = "Operacion exitosa")
    public ResponseEntity<List<Equipo>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(this.equipoService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar equipo por ID", description = "Devuelve un equipo segun su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Equipo encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Equipo.class))),
            @ApiResponse(responseCode = "400", description = "Equipo no encontrado")
    })
    public ResponseEntity<Equipo> findById(
            @Parameter(description = "Id del equipo", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(this.equipoService.findById(id));
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Listar equipos por estado", description = "Filtra equipos activos (true) o inactivos (false)")
    @ApiResponse(responseCode = "200", description = "Operacion exitosa")
    public ResponseEntity<List<Equipo>> findByEstado(
            @Parameter(description = "Estado del equipo: true=activo, false=inactivo", required = true, example = "true")
            @PathVariable Boolean estado) {
        return ResponseEntity.status(HttpStatus.OK).body(this.equipoService.findByEstado(estado));
    }

    @GetMapping("/{id}/miembros")
    @Operation(summary = "Listar miembros de un equipo", description = "Devuelve todos los miembros de un equipo especifico")
    @ApiResponse(responseCode = "200", description = "Operacion exitosa")
    public ResponseEntity<List<MiembroEquipo>> findMiembros(
            @Parameter(description = "Id del equipo", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(this.equipoService.findMiembrosByEquipoId(id));
    }

    @PostMapping
    @Operation(summary = "Crear un equipo", description = "Registra un nuevo equipo. Verifica que el capitan y el juego existan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Equipo creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos o nombre duplicado")
    })
    public ResponseEntity<Equipo> save(@Valid @RequestBody EquipoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.equipoService.save(dto));
    }

    @PostMapping("/miembros")
    @Operation(summary = "Agregar miembro al equipo", description = "Agrega un usuario como miembro de un equipo activo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Miembro agregado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Usuario ya miembro o equipo inactivo")
    })
    public ResponseEntity<MiembroEquipo> agregarMiembro(@Valid @RequestBody MiembroEquipoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.equipoService.agregarMiembro(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un equipo", description = "Actualiza los datos de un equipo existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Equipo actualizado"),
            @ApiResponse(responseCode = "400", description = "Equipo no encontrado")
    })
    public ResponseEntity<Equipo> update(
            @Parameter(description = "Id del equipo a actualizar", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody EquipoDTO dto) {
        return ResponseEntity.status(HttpStatus.OK).body(this.equipoService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar un equipo", description = "Cambia el estado del equipo a inactivo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Equipo desactivado"),
            @ApiResponse(responseCode = "400", description = "Equipo no encontrado")
    })
    public ResponseEntity<Equipo> desactivar(
            @Parameter(description = "Id del equipo a desactivar", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(this.equipoService.desactivar(id));
    }
}
