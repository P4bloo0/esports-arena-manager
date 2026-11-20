package com.esports.notificationservice.clients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@FeignClient(name="team-service",contextId="notificacionEquipo",path="/api/v1/equipos") public interface EquipoClient { record Miembro(Long usuarioId) {} @GetMapping("/{id}/miembros") List<Miembro> miembros(@PathVariable Long id); }
