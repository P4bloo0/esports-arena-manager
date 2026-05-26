package com.esports.rankingservice.clients;

import com.esports.rankingservice.models.dtos.PartidaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "match-service", url = "localhost:8006/api/v1/partidas")
public interface PartidaClient {

    @GetMapping("/{id}")
    PartidaDTO getPartidaById(@PathVariable Long id);
}
