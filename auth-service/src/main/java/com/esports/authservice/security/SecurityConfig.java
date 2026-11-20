package com.esports.authservice.security;
import org.springframework.context.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.esports.common.*;
@Configuration @EnableMethodSecurity @Import(ArenaConfiguration.class)
public class SecurityConfig {
 @Bean SecurityFilterChain filterChain(HttpSecurity http,JwtAuthenticationConverter converter,@Value("${arena.internal-secret}") String internal) throws Exception {
  http.csrf(c -> c.disable()).sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
   .authorizeHttpRequests(a -> a.requestMatchers("/docs/**","/swagger-ui/**","/v3/api-docs/**","/error").permitAll()
    .requestMatchers("/internal/**").hasAuthority("INTERNO")
    .requestMatchers("/api/v1/auth/login","/api/v1/auth/register").permitAll()
 .requestMatchers("/api/v1/auth/admin/**").hasAuthority("ADMINISTRADOR")
 .requestMatchers("/api/v1/auth/**").authenticated().anyRequest().denyAll())
   .oauth2ResourceServer(o -> o.jwt(j -> j.jwtAuthenticationConverter(converter)))
   .exceptionHandling(e -> e.authenticationEntryPoint((req,res,ex) -> { res.setStatus(401); res.setContentType("application/json"); res.getWriter().write("{\"mensaje\":\"Inicia sesion para continuar.\",\"errores\":{}}"); })
    .accessDeniedHandler((req,res,ex) -> { res.setStatus(403); res.setContentType("application/json"); res.getWriter().write("{\"mensaje\":\"Tu rol no tiene permiso.\",\"errores\":{}}"); }))
   .addFilterBefore(new InternalFilter(internal),UsernamePasswordAuthenticationFilter.class);
  return http.build();
 }

 @Bean org.springframework.security.crypto.password.PasswordEncoder passwordEncoder() { return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder(); }
 @Bean org.springframework.security.oauth2.jwt.JwtEncoder jwtEncoder(@Value("${jwt.secret}") String secret) {
  return new org.springframework.security.oauth2.jwt.NimbusJwtEncoder(new com.nimbusds.jose.jwk.source.ImmutableSecret<>(new javax.crypto.spec.SecretKeySpec(secret.getBytes(java.nio.charset.StandardCharsets.UTF_8),"HmacSHA256")));
 }
}
