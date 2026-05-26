package com.esports.matchservice.clients;

import com.esports.matchservice.models.dtos.TorneoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "tournament-service", url = "localhost:8004/api/v1/torneos")
public interface TorneoClient {

    @GetMapping("/{id}")
    TorneoDTO getTorneoById(@PathVariable Long id);
}
