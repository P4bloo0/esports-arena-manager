package com.esports.teamservice.clients;


import com.esports.teamservice.models.dtos.UsuarioDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// lo mismo pero para el jugador :v
@FeignClient(name = "user-service", url = "localhost:8001/api/v1/usuarios")
public interface UsuarioClient {
    @GetMapping("/{id}")
    UsuarioDTO getUsuarioById(@PathVariable Long id);
}