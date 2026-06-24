package com.esports.registrationservice.clients;

import com.esports.registrationservice.models.dtos.UsuarioDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "user-service")
public interface UsuarioClient {

    @GetMapping("/api/v1/usuarios/{id}")
    UsuarioDTO getUsuarioById(@PathVariable("id") Long id);
}