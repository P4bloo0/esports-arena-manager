package com.esports.authservice.services;

import com.esports.authservice.dtos.AuthResponse;
import com.esports.authservice.dtos.LoginRequest;
import com.esports.authservice.dtos.RegisterRequest;
import com.esports.authservice.exceptions.AuthException;
import com.esports.authservice.models.Rol;
import com.esports.authservice.models.Usuario;
import com.esports.authservice.repositories.RolRepository;
import com.esports.authservice.repositories.UsuarioRepository;
import com.esports.authservice.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private Rol rolJugador;
    private Usuario usuarioPrueba;

    @BeforeEach
    public void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setNickname("ProGamer");
        registerRequest.setPassword("clave123");

        loginRequest = new LoginRequest();
        loginRequest.setNickname("ProGamer");
        loginRequest.setPassword("clave123");

        rolJugador = new Rol("JUGADOR");
        rolJugador.setRolId(1L);

        usuarioPrueba = new Usuario();
        usuarioPrueba.setUsuarioId(1L);
        usuarioPrueba.setNickname("ProGamer");
        usuarioPrueba.setPassword("hash-clave123");
        usuarioPrueba.setRoles(Set.of(rolJugador));
    }

    @Test
    @DisplayName("Debe registrar un usuario nuevo con rol por defecto")
    public void shouldRegisterUsuario() {
        when(usuarioRepository.existsByNickname("ProGamer")).thenReturn(false);
        when(rolRepository.findByNombre("JUGADOR")).thenReturn(Optional.of(rolJugador));
        when(passwordEncoder.encode("clave123")).thenReturn("hash-clave123");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));
        when(jwtService.generarToken(any(Usuario.class))).thenReturn("token-jwt");

        AuthResponse result = authService.register(registerRequest);

        assertThat(result.getToken()).isEqualTo("token-jwt");
        assertThat(result.getNickname()).isEqualTo("ProGamer");
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe lanzar excepcion al registrar un nickname repetido")
    public void shouldNotRegisterWhenNicknameRepetido() {
        when(usuarioRepository.existsByNickname("ProGamer")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(AuthException.class);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe lanzar excepcion al registrar con un rol inexistente")
    public void shouldNotRegisterWhenRolNoExiste() {
        registerRequest.setRoles(Set.of("SUPERADMIN"));
        when(usuarioRepository.existsByNickname("ProGamer")).thenReturn(false);
        when(rolRepository.findByNombre("SUPERADMIN")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(AuthException.class);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe autenticar un usuario con credenciales validas")
    public void shouldLoginUsuario() {
        when(usuarioRepository.findByNickname("ProGamer")).thenReturn(Optional.of(usuarioPrueba));
        when(passwordEncoder.matches("clave123", "hash-clave123")).thenReturn(true);
        when(jwtService.generarToken(usuarioPrueba)).thenReturn("token-jwt");

        AuthResponse result = authService.login(loginRequest);

        assertThat(result.getToken()).isEqualTo("token-jwt");
        assertThat(result.getRoles()).contains("JUGADOR");
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando el nickname no existe")
    public void shouldNotLoginWhenNicknameNoExiste() {
        when(usuarioRepository.findByNickname("ProGamer")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(AuthException.class);
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando la contrasena es incorrecta")
    public void shouldNotLoginWhenPasswordIncorrecta() {
        when(usuarioRepository.findByNickname("ProGamer")).thenReturn(Optional.of(usuarioPrueba));
        when(passwordEncoder.matches("clave123", "hash-clave123")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(AuthException.class);
    }
}
