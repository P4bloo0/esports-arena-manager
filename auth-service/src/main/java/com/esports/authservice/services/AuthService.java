package com.esports.authservice.services;
import com.esports.authservice.dtos.*;
import com.esports.authservice.exceptions.AuthException;
import com.esports.authservice.models.*;
import com.esports.authservice.repositories.*;
import com.esports.authservice.security.JwtService;
import com.esports.authservice.clients.PerfilClient;
import com.esports.common.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;
@Service
public class AuthService {
 private final UsuarioRepository usuarios; private final RolRepository roles; private final PasswordEncoder encoder; private final JwtService jwt; private final PerfilClient perfiles;
 public AuthService(UsuarioRepository usuarios,RolRepository roles,PasswordEncoder encoder,JwtService jwt,PerfilClient perfiles) { this.usuarios=usuarios; this.roles=roles; this.encoder=encoder; this.jwt=jwt; this.perfiles=perfiles; }
 public AuthResponse register(RegisterRequest request) {
  if(request.getRoles()!=null && request.getRoles().stream().anyMatch(r -> !"JUGADOR".equalsIgnoreCase(r))) throw new AuthException("El registro publico solo permite el rol JUGADOR.");
  return registerRole(request,"JUGADOR");
 }
 public AuthResponse registerRole(RegisterRequest request,String role) {
  Rol rol=roles.findByNombre(role.toUpperCase()).orElseThrow(() -> new AuthException("Rol no configurado"));
  Usuario user=usuarios.findByNickname(request.getNickname()).orElse(null);
  if(user!=null && user.getRoles().stream().noneMatch(r -> r.getNombre().equals(role.toUpperCase()))) throw new AuthException("El registro pendiente tiene otro rol");
  if(user!=null && (user.getPerfilId()!=null || !encoder.matches(request.getPassword(),user.getPassword()))) throw new AuthException("El nickname ya esta registrado");
  if(user==null) {
   user=new Usuario(); user.setCuentaId(UUID.randomUUID().toString()); user.setNickname(request.getNickname()); user.setPassword(encoder.encode(request.getPassword()));
   user.setNombre(request.getNombre()); user.setEmail(request.getEmail()); user.setRoles(Set.of(rol)); user.setActivo(true); user=usuarios.save(user);
  }
  // Una interrupcion deja la cuenta pendiente; repetir el mismo registro retoma la provision idempotente.
  var perfil=perfiles.crear(new PerfilClient.Provision(user.getCuentaId(),user.getNombre(),user.getNickname(),user.getEmail(),role));
  user.setPerfilId(perfil.usuarioId()); usuarios.save(user); return response(user);
 }
 public AuthResponse login(LoginRequest request) {
  var user=usuarios.findByNickname(request.getNickname()).orElseThrow(() -> new ArenaException(401,"Credenciales invalidas"));
  if(!encoder.matches(request.getPassword(),user.getPassword())) throw new ArenaException(401,"Credenciales invalidas");
  active(user); return response(user);
 }
 public AuthResponse renovar(String nickname) { var user=usuarios.findByNickname(nickname).orElseThrow(() -> new ArenaException(401,"Cuenta no encontrada")); active(user); return response(user); }
 private void active(Usuario user) {
  if(Boolean.FALSE.equals(user.getActivo())) throw new ArenaException(403,"La cuenta esta desactivada");
  if(user.getPerfilId()==null) throw ArenaException.conflict("Completa el registro de la cuenta para ingresar.");
  if(!"ACTIVO".equals(perfiles.buscar(user.getPerfilId()).estado())) throw new ArenaException(403,"La cuenta esta desactivada o sancionada");
 }
 private AuthResponse response(Usuario u) { return new AuthResponse(jwt.generarToken(u),"Bearer",u.getNickname(),u.getRoles().stream().map(Rol::getNombre).collect(Collectors.toSet()),u.getPerfilId()); }
}
