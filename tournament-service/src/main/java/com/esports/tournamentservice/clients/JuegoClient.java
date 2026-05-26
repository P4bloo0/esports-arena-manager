package com.esports.tournamentservice.clients;

import com.esports.tournamentservice.models.dtos.JuegoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "game-service", url = "localhost:8002/api/v1/juegos")
public interface  JuegoClient {
    @GetMapping("/{id}")
    JuegoDTO getJuegoById(@PathVariable Long id);
}
