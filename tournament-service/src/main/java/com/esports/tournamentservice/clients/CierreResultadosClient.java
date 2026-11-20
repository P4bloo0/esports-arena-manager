package com.esports.tournamentservice.clients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@FeignClient(name="result-service",contextId="premiosResultados",path="/api/v1/resultados") public interface CierreResultadosClient { record Resultado(Long partidaId,String estadoValidacion) {} @GetMapping("/torneo/{id}") List<Resultado> listar(@PathVariable Long id); }
