package com.esports.matchservice.clients;

import com.esports.matchservice.models.dtos.InscripcionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;


@FeignClient(name = "registration-service", url = "localhost:8005/api/v1/inscripciones")
public interface InscripcionClient {

    @GetMapping("/torneo/{torneoId}")
    List<InscripcionDTO> getInscripcionesByTorneo(@PathVariable Long torneoId);
}
