package com.esports.tournamentservice.clients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@FeignClient(name="team-service",contextId="premiosEquipo",path="/api/v1/equipos") public interface CierreEquipoClient { record Miembro(Long usuarioId) {} @GetMapping("/{id}/miembros") List<Miembro> miembros(@PathVariable Long id); }
