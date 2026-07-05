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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository, RolRepository rolRepository,
                        PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (usuarioRepository.existsByNickname(request.getNickname())) {
            throw new AuthException("El nickname ya esta registrado");
        }

        Set<Rol> roles = new HashSet<>();
        if (request.getRoles() == null || request.getRoles().isEmpty()) {
            roles.add(rolRepository.findByNombre("JUGADOR")
                    .orElseThrow(() -> new AuthException("Rol JUGADOR no configurado")));
        } else {
            for (String nombreRol : request.getRoles()) {
                Rol rol = rolRepository.findByNombre(nombreRol.toUpperCase())
                        .orElseThrow(() -> new AuthException("El rol " + nombreRol + " no existe"));
                roles.add(rol);
            }
        }

        Usuario usuario = new Usuario();
        usuario.setNickname(request.getNickname());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRoles(roles);
        usuarioRepository.save(usuario);

        return construirRespuesta(usuario);
    }

    public AuthResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByNickname(request.getNickname())
                .orElseThrow(() -> new AuthException("Credenciales invalidas"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new AuthException("Credenciales invalidas");
        }

        return construirRespuesta(usuario);
    }

    private AuthResponse construirRespuesta(Usuario usuario) {
        String token = jwtService.generarToken(usuario);
        Set<String> nombresRoles = usuario.getRoles().stream().map(Rol::getNombre).collect(Collectors.toSet());
        return new AuthResponse(token, "Bearer", usuario.getNickname(), nombresRoles);
    }
}
