package com.esports.rankingservice.clients;

import com.esports.rankingservice.models.dtos.ResultadoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@FeignClient(name = "result-service", url = "localhost:8007/api/v1/resultados")
public interface ResultadoClient {

    @GetMapping("/torneo/{torneoId}")
    List<ResultadoDTO> getResultadosByTorneo(@PathVariable Long torneoId);
}
