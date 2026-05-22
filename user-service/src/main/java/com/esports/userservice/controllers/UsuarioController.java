package com.esports.userservice.controllers;

import com.esports.userservice.models.Usuario;
import com.esports.userservice.models.dtos.UsuarioDTO;
import com.esports.userservice.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    //Get /api/v1/usuarios
    @GetMapping
    public ResponseEntity<List<Usuario>> findAll(){
        return ResponseEntity.ok(usuarioService.findAll());
    }

    //Get /api/v1/usuarios/1
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> findById(@PathVariable Long id){
        return ResponseEntity.ok(usuarioService.findById(id));
    }

    //Get /api/v1/usuarios/rol/jugador
    @GetMapping("/rol/{rol}")
    public ResponseEntity<List<Usuario>> findByRol(@PathVariable Usuario.Rol rol) {
        return ResponseEntity.ok(usuarioService.findByRol(rol));
    }

    //Get /api/v1/usuarios/estado/activo
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Usuario>> findByEstado(@PathVariable Usuario.Estado estado){
        return ResponseEntity.ok(usuarioService.findByEstado(estado));
    }

    //Post /api/v1/usuarios
    @PostMapping
    public ResponseEntity<Usuario> save(@Valid @RequestBody UsuarioDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.save(dto));
    }

    //Put /api/v1/usuarios/1
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> update(@PathVariable Long id, @Valid @RequestBody UsuarioDTO dto){
        return ResponseEntity.ok(usuarioService.update(id, dto));
    }

    //Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Usuario> desactivar(@PathVariable Long id){
        return ResponseEntity.ok(usuarioService.desactivar(id));
    }


}
