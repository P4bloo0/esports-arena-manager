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
        // Given
        when(usuarioRepository.findAll()).thenReturn(List.of(usuarioPrueba));

        // When
        List<Usuario> result = usuarioService.findAll();

        // Then
        assertThat(result).hasSize(1);
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe retornar usuarios filtrados por rol")
    public void shouldFindByRol() {
        // Given
        when(usuarioRepository.findByRol(Usuario.Rol.JUGADOR)).thenReturn(List.of(usuarioPrueba));

        // When
        List<Usuario> result = usuarioService.findByRol(Usuario.Rol.JUGADOR);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRol()).isEqualTo(Usuario.Rol.JUGADOR);
        verify(usuarioRepository, times(1)).findByRol(Usuario.Rol.JUGADOR);
    }

    @Test
    @DisplayName("Debe retornar usuarios filtrados por estado")
    public void shouldFindByEstado() {
        // Given
        when(usuarioRepository.findByEstado(Usuario.Estado.ACTIVO)).thenReturn(List.of(usuarioPrueba));

        // When
        List<Usuario> result = usuarioService.findByEstado(Usuario.Estado.ACTIVO);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEstado()).isEqualTo(Usuario.Estado.ACTIVO);
        verify(usuarioRepository, times(1)).findByEstado(Usuario.Estado.ACTIVO);
    }

    @Test
    @DisplayName("Debe encontrar un usuario por su id")
    public void shouldFindUsuarioById() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioPrueba));

        // When
        Usuario result = usuarioService.findById(1L);

        // Then
        assertThat(result.getNickname()).isEqualTo("juanito123");
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando el usuario no existe")
    public void shouldThrowWhenUsuarioNotFound() {
        // Given
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> usuarioService.findById(999L))
                .isInstanceOf(UsuarioException.class)
                .hasMessageContaining("999");

        verify(usuarioRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Debe guardar un usuario con nickname y email unicos")
    public void shouldSaveUsuario() {
        // Given
        when(usuarioRepository.findByNickname("juanito123")).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail("juan@correo.com")).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioPrueba);

        // When
        Usuario result = usuarioService.save(usuarioDTO);

        // Then
        assertThat(result.getNickname()).isEqualTo("juanito123");
        assertThat(result.getEstado()).isEqualTo(Usuario.Estado.ACTIVO);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe lanzar excepcion al guardar con nickname duplicado")
    public void shouldThrowWhenNicknameDuplicado() {
        // Given
        when(usuarioRepository.findByNickname("juanito123")).thenReturn(Optional.of(usuarioPrueba));

        // When & Then
        assertThatThrownBy(() -> usuarioService.save(usuarioDTO))
                .isInstanceOf(UsuarioException.class)
                .hasMessageContaining("juanito123");

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe lanzar excepcion al guardar con email duplicado")
    public void shouldThrowWhenEmailDuplicado() {
        // Given
        when(usuarioRepository.findByNickname("juanito123")).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail("juan@correo.com")).thenReturn(Optional.of(usuarioPrueba));

        // When & Then
        assertThatThrownBy(() -> usuarioService.save(usuarioDTO))
                .isInstanceOf(UsuarioException.class)
                .hasMessageContaining("juan@correo.com");

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe actualizar los datos de un usuario existente")
    public void shouldUpdateUsuario() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioPrueba));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioDTO dtoActualizado = new UsuarioDTO();
        dtoActualizado.setNombre("Juan Actualizado");
        dtoActualizado.setNickname("juanNuevo");
        dtoActualizado.setEmail("juan@correo.com");
        dtoActualizado.setRol(Usuario.Rol.ORGANIZADOR);

        // When
        Usuario result = usuarioService.update(1L, dtoActualizado);

        // Then
        assertThat(result.getNombre()).isEqualTo("Juan Actualizado");
        assertThat(result.getNickname()).isEqualTo("juanNuevo");
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe desactivar un usuario cambiando su estado a INACTIVO")
    public void shouldDesactivarUsuario() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioPrueba));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        Usuario result = usuarioService.desactivar(1L);

        // Then
        assertThat(result.getEstado()).isEqualTo(Usuario.Estado.INACTIVO);
        verify(usuarioRepository, times(1)).save(usuarioPrueba);
    }
}
