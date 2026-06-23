package com.esports.tournamentservice.services;

import com.esports.tournamentservice.clients.JuegoClient;
import com.esports.tournamentservice.exceptions.TorneoException;
import com.esports.tournamentservice.models.Torneo;
import com.esports.tournamentservice.models.dtos.JuegoDTO;
import com.esports.tournamentservice.models.dtos.TorneoDTO;
import com.esports.tournamentservice.repositories.TorneoRepository;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TorneoServiceImplTest {

    @Mock
    private TorneoRepository torneoRepository;

    @Mock
    private JuegoClient juegoClient;

    @InjectMocks
    private TorneoServiceImpl torneoService;

    private Torneo torneoPrueba;
    private TorneoDTO torneoDTO;

    @BeforeEach
    public void setUp() {
        LocalDateTime cierre = LocalDateTime.now().plusDays(3);
        LocalDateTime inicio = LocalDateTime.now().plusDays(5);
        LocalDateTime fin    = LocalDateTime.now().plusDays(10);

        torneoPrueba = new Torneo();
        torneoPrueba.setTorneoId(1L);
        torneoPrueba.setNombre("Torneo Valorant 2026");
        torneoPrueba.setJuegoId(1L);
        torneoPrueba.setFechaInicio(inicio);
        torneoPrueba.setFechaFin(fin);
        torneoPrueba.setFechaCierreInscripcion(cierre);
        torneoPrueba.setCupoMaximo(16);
        torneoPrueba.setCupoActual(0);
        torneoPrueba.setModalidad("eliminacion_directa");
        torneoPrueba.setEstado("BORRADOR");

        torneoDTO = new TorneoDTO();
        torneoDTO.setNombre("Torneo Valorant 2026");
        torneoDTO.setJuegoId(1L);
        torneoDTO.setFechaInicio(inicio);
        torneoDTO.setFechaFin(fin);
        torneoDTO.setFechaCierreInscripcion(cierre);
        torneoDTO.setCupoMaximo(16);
        torneoDTO.setModalidad("eliminacion_directa");
    }

    @Test
    @DisplayName("Debe retornar todos los torneos")
    public void shouldFindAllTorneos() {
        when(torneoRepository.findAll()).thenReturn(List.of(torneoPrueba));

        List<Torneo> result = torneoService.findAll();

        assertThat(result).hasSize(1);
        verify(torneoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe encontrar un torneo por su id")
    public void shouldFindTorneoById() {
        when(torneoRepository.findById(1L)).thenReturn(Optional.of(torneoPrueba));

        Torneo result = torneoService.findById(1L);

        assertThat(result.getNombre()).isEqualTo("Torneo Valorant 2026");
        verify(torneoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando el torneo no existe")
    public void shouldThrowWhenTorneoNotFound() {
        when(torneoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> torneoService.findById(999L))
                .isInstanceOf(TorneoException.class)
                .hasMessageContaining("999");

        verify(torneoRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Debe guardar un torneo cuando el juego existe")
    public void shouldSaveTorneo() {
        when(juegoClient.getJuegoById(1L)).thenReturn(new JuegoDTO());
        when(torneoRepository.save(any(Torneo.class))).thenReturn(torneoPrueba);

        Torneo result = torneoService.save(torneoDTO);

        assertThat(result.getEstado()).isEqualTo("BORRADOR");
        assertThat(result.getNombre()).isEqualTo("Torneo Valorant 2026");
        verify(torneoRepository, times(1)).save(any(Torneo.class));
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando el juego no existe al crear torneo")
    public void shouldThrowWhenJuegoNoExiste() {
        when(juegoClient.getJuegoById(1L)).thenThrow(mock(FeignException.class));

        assertThatThrownBy(() -> torneoService.save(torneoDTO))
                .isInstanceOf(TorneoException.class)
                .hasMessageContaining("1");

        verify(torneoRepository, never()).save(any(Torneo.class));
    }

    @Test
    @DisplayName("Debe lanzar excepcion al modificar torneo EN_CURSO")
    public void shouldThrowWhenModifyTorneoEnCurso() {
        torneoPrueba.setEstado("EN_CURSO");
        when(torneoRepository.findById(1L)).thenReturn(Optional.of(torneoPrueba));

        assertThatThrownBy(() -> torneoService.update(1L, torneoDTO))
                .isInstanceOf(TorneoException.class)
                .hasMessageContaining("EN_CURSO");

        verify(torneoRepository, never()).save(any(Torneo.class));
    }

    @Test
    @DisplayName("Debe cambiar el estado de un torneo")
    public void shouldCambiarEstadoTorneo() {
        when(torneoRepository.findById(1L)).thenReturn(Optional.of(torneoPrueba));
        when(torneoRepository.save(any(Torneo.class))).thenAnswer(inv -> inv.getArgument(0));

        Torneo result = torneoService.cambiarEstado(1L, "ABIERTO");

        assertThat(result.getEstado()).isEqualTo("ABIERTO");
        verify(torneoRepository, times(1)).save(torneoPrueba);
    }
}