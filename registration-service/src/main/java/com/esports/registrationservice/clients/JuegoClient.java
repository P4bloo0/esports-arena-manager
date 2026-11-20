package com.esports.registrationservice.clients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
@FeignClient(name="game-service",contextId="registrationJuego",path="/api/v1/juegos")
public interface JuegoClient { record Juego(Integer jugadoresPorEquipo,Boolean estado) {} @GetMapping("/{id}") Juego getJuegoById(@PathVariable Long id); }
