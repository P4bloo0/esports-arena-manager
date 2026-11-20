package com.esports.gameservice;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import javax.crypto.spec.SecretKeySpec;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.time.Instant;
import java.util.List;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest class SeguridadApiTest {
 @Autowired WebApplicationContext context; MockMvc mvc;
 static final String BODY="{\"nombre\":\"Duelo\",\"genero\":\"FPS\",\"modalidad\":\"1v1\",\"jugadoresPorEquipo\":1}";
 @BeforeEach void setup() { mvc=MockMvcBuilders.webAppContextSetup(context).apply(SecurityMockMvcConfigurers.springSecurity()).build(); }
 @Test void visitanteConsultaCatalogo() throws Exception { mvc.perform(get("/api/v1/juegos")).andExpect(status().isOk()); }
 @Test void visitanteNoPuedeCrear() throws Exception { mvc.perform(post("/api/v1/juegos").contentType("application/json").content(BODY)).andExpect(status().isUnauthorized()); }
 @Test void jugadorNoPuedeCrear() throws Exception { mvc.perform(post("/api/v1/juegos").header("Authorization",token("JUGADOR")).contentType("application/json").content(BODY)).andExpect(status().isForbidden()); }
 @Test void administradorPuedeCrear() throws Exception { mvc.perform(post("/api/v1/juegos").header("Authorization",token("ADMINISTRADOR")).contentType("application/json").content(BODY)).andExpect(status().isCreated()); }
 @Test void datosInvalidosDevuelvenErroresPorCampo() throws Exception { mvc.perform(post("/api/v1/juegos").header("Authorization",token("ADMINISTRADOR")).contentType("application/json").content("{}")).andExpect(status().isBadRequest()).andExpect(jsonPath("$.errores.nombre").exists()); }
 @Test void recursoInexistenteEs404() throws Exception { mvc.perform(get("/api/v1/juegos/987654")).andExpect(status().isNotFound()); }
 @Test void rutaInternaRechazaCabeceraFaltante() throws Exception { mvc.perform(get("/internal/no-existe")).andExpect(status().isForbidden()); }
 String token(String rol) {
  var key=new SecretKeySpec("solo-para-pruebas-unitarias-no-usar-en-servidores-2026".getBytes(java.nio.charset.StandardCharsets.UTF_8),"HmacSHA256");
  var encoder=new NimbusJwtEncoder(new ImmutableSecret<>(key)); var now=Instant.now();
  return "Bearer "+encoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(),JwtClaimsSet.builder().issuer("auth-service").subject("prueba").issuedAt(now).expiresAt(now.plusSeconds(300)).claim("userId",1).claim("roles",List.of(rol)).build())).getTokenValue();
 }
}
