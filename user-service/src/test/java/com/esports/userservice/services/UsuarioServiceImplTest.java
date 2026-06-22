package com.esports.userservice.services;

import com.esports.userservice.exceptions.UsuarioException;
import com.esports.userservice.models.Usuario;
import com.esports.userservice.models.dtos.UsuarioDTO;
import com.esports.userservice.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private Usuario usuarioPrueba;
    private UsuarioDTO usuarioDTO;

    @BeforeEach
    public void setUp() {
        usuarioPrueba = new Usuario();
        usuarioPrueba.setUsuarioId(1L);
        usuarioPrueba.setNombre("Juan Perez");
        usuarioPrueba.setNickname("juanito123");
        usuarioPrueba.setEmail("juan@correo.com");
        usuarioPrueba.setRol(Usuario.Rol.JUGADOR);
        usuarioPrueba.setEstado(Usuario.Estado.ACTIVO);

        usuarioDTO = new UsuarioDTO();
        usuarioDTO.setNombre("Juan Perez");
        usuarioDTO.setNickname("juanito123");
        usuarioDTO.setEmail("juan@correo.com");
        usuarioDTO.setRol(Usuario.Rol.JUGADOR);
    }

    @Test
    @DisplayName("Debe retornar todos los usuarios")
    public void shouldFindAllUsuarios() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuarioPrueba));

        List<Usuario> result = usuarioService.findAll();

        assertThat(result).hasSize(1);
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe encontrar un usuario por su id")
    public void shouldFindUsuarioById() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioPrueba));

        Usuario result = usuarioService.findById(1L);

        assertThat(result.getNickname()).isEqualTo("juanito123");
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando el usuario no existe")
    public void shouldThrowWhenUsuarioNotFound() {
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.findById(999L))
                .isInstanceOf(UsuarioException.class)
                .hasMessageContaining("999");

        verify(usuarioRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Debe guardar un usuario con nickname y email unicos")
    public void shouldSaveUsuario() {
        when(usuarioRepository.findByNickname("juanito123")).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail("juan@correo.com")).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioPrueba);

        Usuario result = usuarioService.save(usuarioDTO);

        assertThat(result.getNickname()).isEqualTo("juanito123");
        assertThat(result.getEstado()).isEqualTo(Usuario.Estado.ACTIVO);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe lanzar excepcion al guardar con nickname duplicado")
    public void shouldThrowWhenNicknameDuplicado() {
        when(usuarioRepository.findByNickname("juanito123")).thenReturn(Optional.of(usuarioPrueba));

        assertThatThrownBy(() -> usuarioService.save(usuarioDTO))
                .isInstanceOf(UsuarioException.class)
                .hasMessageContaining("juanito123");

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe lanzar excepcion al guardar con email duplicado")
    public void shouldThrowWhenEmailDuplicado() {
        when(usuarioRepository.findByNickname("juanito123")).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail("juan@correo.com")).thenReturn(Optional.of(usuarioPrueba));

        assertThatThrownBy(() -> usuarioService.save(usuarioDTO))
                .isInstanceOf(UsuarioException.class)
                .hasMessageContaining("juan@correo.com");

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe desactivar un usuario cambiando su estado a INACTIVO")
    public void shouldDesactivarUsuario() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioPrueba));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario result = usuarioService.desactivar(1L);

        assertThat(result.getEstado()).isEqualTo(Usuario.Estado.INACTIVO);
        verify(usuarioRepository, times(1)).save(usuarioPrueba);
    }
}