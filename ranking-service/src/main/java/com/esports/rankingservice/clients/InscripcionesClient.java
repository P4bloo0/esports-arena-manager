package com.esports.rankingservice.clients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@FeignClient(name="registration-service",contextId="rankingInscripciones",path="/api/v1/inscripciones")
public interface InscripcionesClient { record Inscripcion(Long equipoId,Long jugadorId,String estado) {} @GetMapping("/torneo/{id}") List<Inscripcion> listar(@PathVariable Long id); }
