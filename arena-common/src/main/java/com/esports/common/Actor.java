package com.esports.common;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
public final class Actor {
 private Actor() {}
 public static boolean staff() { var a=SecurityContextHolder.getContext().getAuthentication(); return a!=null && a.getAuthorities().stream().anyMatch(r -> java.util.Set.of("ADMINISTRADOR","ORGANIZADOR","INTERNO").contains(r.getAuthority())); }
 public static Long id() {
  var a=SecurityContextHolder.getContext().getAuthentication();
  if(a!=null && a.getPrincipal() instanceof Jwt jwt && jwt.getClaim("userId") != null) return Long.valueOf(jwt.getClaim("userId").toString());
  throw new ArenaException(401,"Inicia sesion con una cuenta vinculada a un jugador.");
 }
 public static void own(Long id) { if(!staff() && !id().equals(id)) throw new ArenaException(403,"No puedes modificar datos de otro jugador."); }
 public static void captain(Long captainId) { own(captainId); }
}
