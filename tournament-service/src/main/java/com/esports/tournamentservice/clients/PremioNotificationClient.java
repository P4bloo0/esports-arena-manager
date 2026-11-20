package com.esports.tournamentservice.clients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
@FeignClient(name="notification-service",contextId="premiosNotificacion",path="/internal/notificaciones") public interface PremioNotificationClient { record Notificacion(Long usuarioId,String tipo,String mensaje,String clave) {} @PostMapping void enviar(@RequestBody Notificacion dto); }
