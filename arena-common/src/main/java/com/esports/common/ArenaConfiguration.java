package com.esports.common;
import org.springframework.context.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.server.resource.authentication.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.web.context.request.*;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;
@Configuration
@Import(ArenaErrors.class)
public class ArenaConfiguration {
 @Bean JwtDecoder jwtDecoder(@Value("${jwt.secret}") String secret) {
  if(secret.length()<32) throw new IllegalArgumentException("JWT_SECRET requiere al menos 32 caracteres");
  var decoder=NimbusJwtDecoder.withSecretKey(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),"HmacSHA256")).macAlgorithm(MacAlgorithm.HS256).build();
  decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer("auth-service")); return decoder;
 }
 @Bean JwtAuthenticationConverter jwtAuthenticationConverter() {
  var roles=new JwtGrantedAuthoritiesConverter(); roles.setAuthoritiesClaimName("roles"); roles.setAuthorityPrefix("");
  var converter=new JwtAuthenticationConverter(); converter.setJwtGrantedAuthoritiesConverter(roles); return converter;
 }
 @Bean feign.RequestInterceptor arenaHeaders(@Value("${arena.internal-secret}") String internal) {
  return request -> {
   // Feign aplica el destino completo despues de los interceptores. El prefijo de
   // @FeignClient(path=...) puede estar en el destino y no en la plantilla.
   var targetPath=request.feignTarget()==null?"":java.net.URI.create(request.feignTarget().url()).getPath();
   var path=targetPath+java.net.URI.create(request.url()).getPath();
   if(path.startsWith("/internal/")) request.header("X-Arena-Internal",internal);
   else if(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs) {
    var token=attrs.getRequest().getHeader("Authorization"); if(token!=null) request.header("Authorization",token);
   }
  };
 }
 @Bean feign.codec.ErrorDecoder remoteErrors() {
  var mapper = JsonMapper.builder().build();
  return (method,response) -> {
   String message="El servicio remoto no pudo completar la operacion.";
   Map<String,String> errors = new LinkedHashMap<>();
   if (response.body() != null) {
    try (var body = response.body().asInputStream()) {
     var json = mapper.readTree(body);
     if (json.path("mensaje").isTextual()) message = json.path("mensaje").asText();
     for (var campo : json.path("errores").properties()) {
      if (campo.getValue().isTextual()) errors.put(campo.getKey(), campo.getValue().asText());
     }
    } catch (IOException | JacksonException ignored) {
     // Algunos errores de infraestructura no tienen un cuerpo JSON.
    }
   }
   return new ArenaException(response.status(),message,errors);
  };
 }
}
