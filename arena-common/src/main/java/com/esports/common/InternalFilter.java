package com.esports.common;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
public class InternalFilter extends OncePerRequestFilter {
 private final String secret;
 public InternalFilter(String secret) { if(secret==null || secret.length()<32) throw new IllegalArgumentException("ARENA_INTERNAL_SECRET requiere al menos 32 caracteres"); this.secret=secret; }
 @Override protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain chain) throws ServletException,IOException {
  if(req.getRequestURI().startsWith("/internal/")) {
   var header=req.getHeader("X-Arena-Internal");
   if(header==null || !MessageDigest.isEqual(secret.getBytes(StandardCharsets.UTF_8),header.getBytes(StandardCharsets.UTF_8))) {
    res.setStatus(403); res.setContentType("application/json"); res.getWriter().write("{\"mensaje\":\"Acceso interno no autorizado\",\"errores\":{}}"); return;
   }
   SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("internal",null,List.of(new SimpleGrantedAuthority("INTERNO"))));
  }
  chain.doFilter(req,res);
 }
}
