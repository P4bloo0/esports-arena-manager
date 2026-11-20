package com.esports.userservice.controllers;
import com.esports.userservice.models.Usuario;
import com.esports.userservice.models.dtos.UsuarioDTO;
import com.esports.userservice.repositories.UsuarioRepository;
import com.esports.userservice.services.UsuarioService;
import com.esports.common.*;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;
@RestController
public class PerfilController {
 private final UsuarioRepository repo; private final UsuarioService service;
 public PerfilController(UsuarioRepository repo,UsuarioService service) { this.repo=repo; this.service=service; }
 public record Provision(@NotBlank String cuentaId,@NotBlank String nombre,@NotBlank @Size(min=3,max=16) @Pattern(regexp="^[^\\s]+$") String nickname,@Email @NotBlank String email,@NotNull Usuario.Rol rol) {}
 public record Jugador(Long usuarioId,String nickname,String nombre) {}
 @org.springframework.transaction.annotation.Transactional
 @PostMapping("/internal/usuarios") public Usuario provision(@Valid @RequestBody Provision p) {
  return repo.findByCuentaId(p.cuentaId()).orElseGet(() -> {
   var dto=new UsuarioDTO(); dto.setNombre(p.nombre()); dto.setNickname(p.nickname()); dto.setEmail(p.email()); dto.setRol(p.rol());
   var user=service.save(dto); user.setCuentaId(p.cuentaId()); return repo.save(user);
  });
 }
 @GetMapping("/internal/usuarios/{id}") public Usuario internal(@PathVariable Long id) { return service.findById(id); }
 @PutMapping("/internal/usuarios/{id}/rol") public Usuario role(@PathVariable Long id,@RequestParam Usuario.Rol rol) { var u=service.findById(id); u.setRol(rol); return repo.save(u); }
 @GetMapping("/api/v1/usuarios/me") public Usuario me() { return service.findById(Actor.id()); }
 @GetMapping("/api/v1/usuarios/jugadores") public List<Jugador> players() { return repo.findByRol(Usuario.Rol.JUGADOR).stream().map(u -> new Jugador(u.getUsuarioId(),u.getNickname(),u.getNombre())).toList(); }
}
