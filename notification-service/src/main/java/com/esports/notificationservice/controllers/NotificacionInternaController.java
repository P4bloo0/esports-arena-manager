package com.esports.notificationservice.controllers;
import com.esports.notificationservice.models.*;
import com.esports.notificationservice.models.dtos.*;
import com.esports.notificationservice.services.NotificacionService;
import com.esports.common.Actor;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
@RestController public class NotificacionInternaController {
 private final NotificacionService service; public NotificacionInternaController(NotificacionService service) { this.service=service; }
 @PostMapping("/internal/notificaciones") public Notificacion enviar(@Valid @RequestBody NotificacionDTO dto) { return service.save(dto); }
 @GetMapping("/api/v1/notificaciones/me") public List<Notificacion> mine() { return service.findByUsuarioId(Actor.id()); }
}
