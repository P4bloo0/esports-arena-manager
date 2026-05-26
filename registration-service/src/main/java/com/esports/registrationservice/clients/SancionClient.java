package com.esports.registrationservice.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Map;


@FeignClient(name = "sanction-service", url = "localhost:8009/api/v1/sanciones")
public interface SancionClient {

    @GetMapping("/verificar")
    Map<String, Boolean> verificarSancion(@RequestParam(required = false) Long usuarioId,
                                          @RequestParam(required = false) Long equipoId);
}
