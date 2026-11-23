package com.esports.authservice.services;
import com.esports.authservice.dtos.*;
import com.esports.authservice.models.*;
import com.esports.authservice.repositories.*;
import com.esports.authservice.security.JwtService;
import com.esports.authservice.clients.PerfilClient;
import com.esports.authservice.exceptions.AuthException;
import com.esports.common.ArenaException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
 @Mock UsuarioRepository usuarios; @Mock RolRepository roles; @Mock PasswordEncoder encoder; @Mock JwtService jwt; @Mock PerfilClient perfiles;
 @InjectMocks AuthService service;
 RegisterRequest request; Usuario user; Rol jugador;
 @BeforeEach void setup() {
  request=new RegisterRequest(); request.setNickname("ProGamer"); request.setNombre("Jugador de prueba"); request.setEmail("jugador@example.test"); request.setPassword("clave1234");
  jugador=new Rol("JUGADOR"); user=new Usuario(); user.setUsuarioId(5L); user.setNickname("ProGamer"); user.setPassword("hash"); user.setRoles(Set.of(jugador)); user.setActivo(true); user.setPerfilId(19L);
 }
 @Test void registraPerfilYDevuelveSuIdentidad() {
  when(roles.findByNombre("JUGADOR")).thenReturn(Optional.of(jugador)); when(encoder.encode("clave1234")).thenReturn("hash");
  when(usuarios.save(any())).thenAnswer(i -> i.getArgument(0)); when(perfiles.crear(any())).thenReturn(perfil("ACTIVO")); when(jwt.generarToken(any())).thenReturn("token");
  var respuesta=service.register(request); assertThat(respuesta.userId()).isEqualTo(19); assertThat(respuesta.roles()).containsExactly("JUGADOR");
  verify(perfiles).crear(argThat(p -> p.email().equals("jugador@example.test") && p.cuentaId()!=null));
 }
 @Test void registroPublicoNoPermiteAdministrador() { request.setRoles(Set.of("ADMINISTRADOR")); assertThatThrownBy(() -> service.register(request)).isInstanceOf(AuthException.class); verifyNoInteractions(usuarios,perfiles,jwt); }
 @Test void registroPublicoNoPermiteRolDesconocido() { request.setRoles(Set.of("SUPERADMIN")); assertThatThrownBy(() -> service.register(request)).isInstanceOf(AuthException.class); }
 @Test void noRegistraNicknameExistente() { when(roles.findByNombre("JUGADOR")).thenReturn(Optional.of(jugador)); when(usuarios.findByNickname("ProGamer")).thenReturn(Optional.of(user)); assertThatThrownBy(() -> service.register(request)).isInstanceOf(AuthException.class); verifyNoInteractions(perfiles); }
 @Test void loginValidoVerificaElEstadoDelPerfil() { login(); when(perfiles.buscar(19L)).thenReturn(perfil("ACTIVO")); when(jwt.generarToken(user)).thenReturn("token"); assertThat(service.login(credentials()).userId()).isEqualTo(19); }
 @Test void loginConPasswordIncorrectaEs401() { when(usuarios.findByNickname("ProGamer")).thenReturn(Optional.of(user)); assertThatThrownBy(() -> service.login(credentials())).isInstanceOfSatisfying(ArenaException.class,e -> assertThat(e.status()).isEqualTo(401)); }
 @Test void loginConCuentaInexistenteEs401() { assertThatThrownBy(() -> service.login(credentials())).isInstanceOfSatisfying(ArenaException.class,e -> assertThat(e.status()).isEqualTo(401)); }
 @Test void cuentaDesactivadaEs403() { login(); user.setActivo(false); assertThatThrownBy(() -> service.login(credentials())).isInstanceOfSatisfying(ArenaException.class,e -> assertThat(e.status()).isEqualTo(403)); }
 @Test void perfilDesactivadoNoPuedeIngresar() { login(); when(perfiles.buscar(19L)).thenReturn(perfil("INACTIVO")); assertThatThrownBy(() -> service.login(credentials())).isInstanceOfSatisfying(ArenaException.class,e -> assertThat(e.status()).isEqualTo(403)); }
 @Test void puedeCorregirElCorreoDeUnaCuentaPendiente() {
  user.setPerfilId(null);
  user.setCuentaId("cuenta-estable");
  user.setEmail("ocupado@example.test");
  when(roles.findByNombre("JUGADOR")).thenReturn(Optional.of(jugador));
  login();
  when(perfiles.crear(any())).thenReturn(perfil("ACTIVO"));

  assertThat(service.register(request).userId()).isEqualTo(19L);

  verify(perfiles).crear(argThat(p -> p.cuentaId().equals("cuenta-estable")
      && p.email().equals("jugador@example.test")));
  assertThat(user.getEmail()).isEqualTo("jugador@example.test");
 }

 @Test void alReintentarConservaLosDatosDeUnPerfilYaCreado() {
  user.setPerfilId(null);
  user.setCuentaId("cuenta-estable");
  when(roles.findByNombre("JUGADOR")).thenReturn(Optional.of(jugador));
  login();
  request.setEmail("otro@example.test");
  when(perfiles.crear(any())).thenReturn(perfil("ACTIVO"));

  service.register(request);

  assertThat(user.getPerfilId()).isEqualTo(19L);
  assertThat(user.getEmail()).isEqualTo("jugador@example.test");
 }

 PerfilClient.Perfil perfil(String estado) {
  return new PerfilClient.Perfil(19L, estado, "Jugador de prueba", "jugador@example.test");
 }
 void login() { when(usuarios.findByNickname("ProGamer")).thenReturn(Optional.of(user)); when(encoder.matches("clave1234","hash")).thenReturn(true); }
 LoginRequest credentials() { var r=new LoginRequest(); r.setNickname("ProGamer"); r.setPassword("clave1234"); return r; }
}
