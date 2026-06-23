package com.esports.userservice.controllers;

import com.esports.userservice.models.Usuario;
import com.esports.userservice.models.dtos.UsuarioDTO;
import com.esports.userservice.services.UsuarioService;
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
@RequestMapping("/api/v1/usuarios")
@Validated
@Tag(name = "Usuarios", description = "Metodos CRUD para la gestion de usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    @Operation(summary = "Listado de todos los usuarios", description = "Se devuelve una lista con todos los usuarios registrados")
    @ApiResponse(responseCode = "200", description = "Operacion Exitosa")
    public ResponseEntity<List<Usuario>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(this.usuarioService.findAll());
    }

    @GetMapping("/rol/{rol}")
    @Operation(summary = "Listar usuarios por rol", description = "Filtra usuarios segun su rol: JUGADOR, ORGANIZADOR o ADMINISTRADOR")
    @ApiResponse(responseCode = "200", description = "Operacion Exitosa")
    public ResponseEntity<List<Usuario>> findByRol(
            @Parameter(description = "Rol del usuario", required = true, example = "JUGADOR")
            @PathVariable Usuario.Rol rol) {
        return ResponseEntity.status(HttpStatus.OK).body(this.usuarioService.findByRol(rol));
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Listar usuarios por estado", description = "Filtra usuarios segun su estado: ACTIVO, INACTIVO o SANCIONADO")
    @ApiResponse(responseCode = "200", description = "Operacion Exitosa")
    public ResponseEntity<List<Usuario>> findByEstado(
            @Parameter(description = "Estado del usuario", required = true, example = "ACTIVO")
            @PathVariable Usuario.Estado estado) {
        return ResponseEntity.status(HttpStatus.OK).body(this.usuarioService.findByEstado(estado));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuario por ID", description = "Se devuelve un usuario segun su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "400", description = "Usuario no encontrado")
    })
    public ResponseEntity<Usuario> findById(
            @Parameter(description = "Id del usuario a buscar", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(this.usuarioService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Crear un usuario", description = "Registra un nuevo usuario. Nickname y email deben ser unicos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos o nickname/email duplicado")
    })
    public ResponseEntity<Usuario> save(@Valid @RequestBody UsuarioDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.usuarioService.save(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un usuario", description = "Actualiza los datos de un usuario existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado"),
            @ApiResponse(responseCode = "400", description = "Usuario no encontrado")
    })
    public ResponseEntity<Usuario> update(
            @Parameter(description = "Id del usuario a actualizar", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody UsuarioDTO dto) {
        return ResponseEntity.status(HttpStatus.OK).body(this.usuarioService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar un usuario", description = "Cambia el estado del usuario a INACTIVO")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario desactivado"),
            @ApiResponse(responseCode = "400", description = "Usuario no encontrado")
    })
    public ResponseEntity<Usuario> desactivar(
            @Parameter(description = "Id del usuario a desactivar", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(this.usuarioService.desactivar(id));
    }
}