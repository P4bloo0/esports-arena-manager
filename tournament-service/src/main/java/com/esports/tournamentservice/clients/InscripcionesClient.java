package com.esports.tournamentservice.clients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
@FeignClient(name="registration-service",contextId="torneoCupos",path="/api/v1/inscripciones") public interface InscripcionesClient { @GetMapping("/cupos/{id}") long ocupados(@PathVariable Long id); }
