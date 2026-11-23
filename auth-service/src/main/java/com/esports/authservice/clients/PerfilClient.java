package com.esports.authservice.clients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
@FeignClient(name="user-service",contextId="authPerfiles",path="/internal/usuarios")
public interface PerfilClient {
 record Provision(String cuentaId,String nombre,String nickname,String email,String rol) {}
 record Perfil(Long usuarioId, String estado, String nombre, String email) {}
 @PostMapping Perfil crear(@RequestBody Provision p);
 @GetMapping("/{id}") Perfil buscar(@PathVariable Long id);
 @PutMapping("/{id}/rol") Perfil rol(@PathVariable Long id,@RequestParam String rol);
}
