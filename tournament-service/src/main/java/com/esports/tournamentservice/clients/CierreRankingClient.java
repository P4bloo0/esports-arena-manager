package com.esports.tournamentservice.clients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@FeignClient(name="ranking-service",contextId="premiosRanking",path="/internal/rankings")
public interface CierreRankingClient { record Posicion(Long participanteId,Integer posicion) {} @PostMapping("/{id}/recalcular") List<Posicion> recalcular(@PathVariable Long id); }
