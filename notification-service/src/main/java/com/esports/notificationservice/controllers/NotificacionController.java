package com.esports.notificationservice.controllers;

import com.esports.notificationservice.models.Notificacion;
import com.esports.notificationservice.models.dtos.NotificacionDTO;
import com.esports.notificationservice.services.NotificacionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/v1/notificaciones")
@Validated
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    // GET /api/v1/notificaciones
    @GetMapping
    public ResponseEntity<List<Notificacion>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(this.notificacionService.findAll());
    }

    // GET /api/v1/notificaciones/1
    @GetMapping("/{id}")
    public ResponseEntity<Notificacion> findById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(this.notificacionService.findById(id));
    }

    // GET /api/v1/notificaciones/usuario/1
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Notificacion>> findByUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.status(HttpStatus.OK).body(this.notificacionService.findByUsuarioId(usuarioId));
    }

    // GET /api/v1/notificaciones/equipo/1
    @GetMapping("/equipo/{equipoId}")
    public ResponseEntity<List<Notificacion>> findByEquipo(@PathVariable Long equipoId) {
        return ResponseEntity.status(HttpStatus.OK).body(this.notificacionService.findByEquipoId(equipoId));
    }

    // GET /api/v1/notificaciones/usuario/1/no-leidas
    @GetMapping("/usuario/{usuarioId}/no-leidas")
    public ResponseEntity<List<Notificacion>> findNoLeidas(@PathVariable Long usuarioId) {
        return ResponseEntity.status(HttpStatus.OK).body(this.notificacionService.findNoLeidasByUsuarioId(usuarioId));
    }

    // POST /api/v1/notificaciones
    @PostMapping
    public ResponseEntity<Notificacion> save(@Valid @RequestBody NotificacionDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.notificacionService.save(dto));
    }

    // PATCH /api/v1/notificaciones/1/leer
    @PatchMapping("/{id}/leer")
    public ResponseEntity<Notificacion> marcarComoLeida(@PathVariable Long id) {
        return ResponseEntity.ok(this.notificacionService.marcarComoLeida(id));
    }

    // PATCH /api/v1/notificaciones/1/archivar
    @PatchMapping("/{id}/archivar")
    public ResponseEntity<Notificacion> archivar(@PathVariable Long id) {
        return ResponseEntity.ok(this.notificacionService.archivar(id));
    }
}
