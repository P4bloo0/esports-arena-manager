package com.esports.gameservice.services;

import com.esports.gameservice.exceptions.JuegoException;
import com.esports.gameservice.models.Juego;
import com.esports.gameservice.models.dtos.JuegoDTO;
import com.esports.gameservice.repositories.JuegoRepository;
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
public class JuegoServiceImplTest {

    @Mock
    private JuegoRepository juegoRepository;

    @InjectMocks
    private JuegoServiceImpl juegoService;

    private Juego juegoPrueba;
    private JuegoDTO juegoDTO;

    @BeforeEach
    public void setUp() {
        juegoPrueba = new Juego();
        juegoPrueba.setJuegoId(1L);
        juegoPrueba.setNombre("Valorant");
        juegoPrueba.setGenero("FPS");
        juegoPrueba.setModalidad("5v5");
        juegoPrueba.setJugadoresPorEquipo(5);
        juegoPrueba.setEstado(true);

        juegoDTO = new JuegoDTO();
        juegoDTO.setNombre("Valorant");
        juegoDTO.setGenero("FPS");
        juegoDTO.setModalidad("5v5");
        juegoDTO.setJugadoresPorEquipo(5);
    }

    @Test
    @DisplayName("Debe retornar todos los juegos")
    public void shouldFindAllJuegos() {
        // Given
        when(juegoRepository.findAll()).thenReturn(List.of(juegoPrueba));

        // When
        List<Juego> result = juegoService.findAll();

        // Then
        assertThat(result).hasSize(1);
        verify(juegoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe retornar solo los juegos activos")
    public void shouldFindAllActivos() {
        // Given
        when(juegoRepository.findByEstado(true)).thenReturn(List.of(juegoPrueba));

        // When
        List<Juego> result = juegoService.findAllActivos();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEstado()).isTrue();
        verify(juegoRepository, times(1)).findByEstado(true);
    }

    @Test
    @DisplayName("Debe encontrar un juego por su id")
    public void shouldFindJuegoById() {
        // Given
        when(juegoRepository.findById(1L)).thenReturn(Optional.of(juegoPrueba));

        // When
        Juego result = juegoService.findById(1L);

        // Then
        assertThat(result.getJuegoId()).isEqualTo(1L);
        assertThat(result.getNombre()).isEqualTo("Valorant");
        verify(juegoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar JuegoException cuando el juego no existe")
    public void shouldThrowExceptionWhenJuegoNotFound() {
        // Given
        when(juegoRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> juegoService.findById(999L))
                .isInstanceOf(JuegoException.class)
                .hasMessageContaining("999");

        verify(juegoRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Debe guardar un juego nuevo exitosamente")
    public void shouldSaveJuego() {
        // Given
        when(juegoRepository.findByNombre("Valorant")).thenReturn(Optional.empty());
        when(juegoRepository.save(any(Juego.class))).thenReturn(juegoPrueba);

        // When
        Juego result = juegoService.save(juegoDTO);

        // Then
        assertThat(result.getNombre()).isEqualTo("Valorant");
        assertThat(result.getEstado()).isTrue();
        verify(juegoRepository, times(1)).save(any(Juego.class));
    }

    @Test
    @DisplayName("Debe lanzar JuegoException al guardar con nombre duplicado")
    public void shouldThrowExceptionWhenNombreDuplicado() {
        // Given
        when(juegoRepository.findByNombre("Valorant")).thenReturn(Optional.of(juegoPrueba));

        // When & Then
        assertThatThrownBy(() -> juegoService.save(juegoDTO))
                .isInstanceOf(JuegoException.class)
                .hasMessageContaining("Valorant");

        verify(juegoRepository, never()).save(any(Juego.class));
    }

    @Test
    @DisplayName("Debe actualizar los datos de un juego existente")
    public void shouldUpdateJuego() {
        // Given
        when(juegoRepository.findById(1L)).thenReturn(Optional.of(juegoPrueba));
        when(juegoRepository.save(any(Juego.class))).thenAnswer(inv -> inv.getArgument(0));

        JuegoDTO dtoActualizado = new JuegoDTO();
        dtoActualizado.setNombre("Valorant");
        dtoActualizado.setGenero("FPS Tactico");
        dtoActualizado.setModalidad("5v5 Competitivo");
        dtoActualizado.setJugadoresPorEquipo(5);

        // When
        Juego result = juegoService.update(1L, dtoActualizado);

        // Then
        assertThat(result.getGenero()).isEqualTo("FPS Tactico");
        assertThat(result.getModalidad()).isEqualTo("5v5 Competitivo");
        verify(juegoRepository, times(1)).save(any(Juego.class));
    }

    @Test
    @DisplayName("Debe lanzar excepcion al actualizar un juego que no existe")
    public void shouldThrowWhenUpdateJuegoNotFound() {
        // Given
        when(juegoRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> juegoService.update(999L, juegoDTO))
                .isInstanceOf(JuegoException.class)
                .hasMessageContaining("999");

        verify(juegoRepository, never()).save(any(Juego.class));
    }

    @Test
    @DisplayName("Debe desactivar un juego cambiando su estado a false")
    public void shouldDesactivarJuego() {
        // Given
        when(juegoRepository.findById(1L)).thenReturn(Optional.of(juegoPrueba));
        when(juegoRepository.save(any(Juego.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        Juego result = juegoService.desactivar(1L);

        // Then
        assertThat(result.getEstado()).isFalse();
        verify(juegoRepository, times(1)).save(juegoPrueba);
    }

    @Test
    @DisplayName("Debe lanzar excepcion al desactivar un juego que no existe")
    public void shouldThrowWhenDesactivarJuegoNotFound() {
        // Given
        when(juegoRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> juegoService.desactivar(999L))
                .isInstanceOf(JuegoException.class)
                .hasMessageContaining("999");

        verify(juegoRepository, never()).save(any(Juego.class));
    }
}
