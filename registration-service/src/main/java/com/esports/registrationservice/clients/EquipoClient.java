package com.esports.registrationservice.clients;

import com.esports.registrationservice.models.dtos.EquipoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "team-service", path = "/api/v1/equipos")
public interface EquipoClient {

    @GetMapping("/{id}")
    EquipoDTO getEquipoById(@PathVariable Long id);
    record Miembro(Long usuarioId) {}
    @GetMapping("/{id}/miembros") java.util.List<Miembro> miembros(@PathVariable Long id);
}
