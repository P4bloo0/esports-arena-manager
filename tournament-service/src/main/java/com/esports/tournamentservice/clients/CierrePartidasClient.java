package com.esports.tournamentservice.clients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@FeignClient(name="match-service",contextId="premiosPartidas",path="/api/v1/partidas") public interface CierrePartidasClient { record Partida(Long partidaId,String estado) {} @GetMapping("/torneo/{id}") List<Partida> listar(@PathVariable Long id); }
