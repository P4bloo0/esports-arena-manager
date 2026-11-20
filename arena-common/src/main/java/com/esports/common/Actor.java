package com.esports.common;

import java.util.Set;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

public final class Actor {
 private Actor() {}

 private static boolean hasAny(Set<String> roles) {
  var auth = SecurityContextHolder.getContext().getAuthentication();
  return auth != null && auth.isAuthenticated()
          && auth.getAuthorities().stream()
          .anyMatch(a -> roles.contains(a.getAuthority()));
 }

 public static boolean admin() {
  return hasAny(Set.of("ADMINISTRADOR", "INTERNO"));
 }

 public static boolean staff() {
  return hasAny(Set.of("ADMINISTRADOR", "ORGANIZADOR", "INTERNO"));
 }

 public static Long id() {
  var auth = SecurityContextHolder.getContext().getAuthentication();
  if (auth != null && auth.getPrincipal() instanceof Jwt jwt
          && jwt.getClaim("userId") != null) {
   return Long.valueOf(jwt.getClaim("userId").toString());
  }
  throw new ArenaException(
          401, "Inicia sesion con una cuenta vinculada a un jugador.");
 }

 public static void own(Long userId) {
  if (!admin() && !id().equals(userId)) {
   throw new ArenaException(
           403, "No puedes modificar datos de otro jugador.");
  }
 }

 public static void captain(Long captainId) {
  own(captainId);
 }
}