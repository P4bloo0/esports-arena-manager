package com.esports.authservice.controllers;
import com.esports.authservice.dtos.*;
import com.esports.authservice.services.AuthService;
import com.esports.authservice.repositories.UsuarioRepository;
import com.esports.common.*;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
@RestController
public class BootstrapController {
 private final AuthService auth; private final UsuarioRepository users;
 public BootstrapController(AuthService auth,UsuarioRepository users) { this.auth=auth; this.users=users; }
 @PostMapping("/internal/auth/bootstrap") public synchronized AuthResponse bootstrap(@Valid @RequestBody RegisterRequest request) {
  if(users.findAll().stream().anyMatch(u -> u.getPerfilId()!=null && u.getRoles().stream().anyMatch(r -> r.getNombre().equals("ADMINISTRADOR")))) throw ArenaException.conflict("El administrador inicial ya existe.");
  return auth.registerRole(request,"ADMINISTRADOR");
 }
}
