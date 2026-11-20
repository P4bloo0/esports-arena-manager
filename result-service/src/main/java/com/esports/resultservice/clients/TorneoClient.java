package com.esports.resultservice.clients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
@FeignClient(name="tournament-service",contextId="resultadoTorneo",path="/api/v1/torneos") public interface TorneoClient { record Torneo(String estado) {} @GetMapping("/{id}") Torneo buscar(@PathVariable Long id); }
