package com.esports.resultservice.clients;

import com.esports.resultservice.models.dtos.PartidaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;


@FeignClient(name = "match-service", url = "localhost:8006/api/v1/partidas")
public interface PartidaClient {

    @GetMapping("/{id}")
    PartidaDTO getPartidaById(@PathVariable Long id);

    @GetMapping("/torneo/{torneoId}")
    List<PartidaDTO> getPartidasByTorneo(@PathVariable Long torneoId);
}
