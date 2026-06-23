package com.esports.matchservice.services;

import com.esports.matchservice.clients.InscripcionClient;
import com.esports.matchservice.clients.TorneoClient;
import com.esports.matchservice.exceptions.PartidaException;
import com.esports.matchservice.models.Partida;
import com.esports.matchservice.models.dtos.InscripcionDTO;
import com.esports.matchservice.models.dtos.PartidaDTO;
import com.esports.matchservice.models.dtos.TorneoDTO;
import com.esports.matchservice.repositories.PartidaRepository;
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
public class PartidaServiceImplTest {

    @Mock
    private PartidaRepository partidaRepository;

    @Mock
    private TorneoClient torneoClient;

    @Mock
    private InscripcionClient inscripcionClient;

    @InjectMocks
    private PartidaServiceImpl partidaService;

    private Partida partidaPrueba;
    private PartidaDTO partidaDTO;
    private TorneoDTO torneoDTO;
    private List<InscripcionDTO> inscripcionesMock;

    @BeforeEach
    public void setUp() {
        partidaPrueba = new Partida();
        partidaPrueba.setPartidaId(1L);
        partidaPrueba.setTorneoId(1L);
        partidaPrueba.setParticipanteAId(10L);
        partidaPrueba.setParticipanteBId(20L);
        partidaPrueba.setRonda("SEMIFINAL");
        partidaPrueba.setFechaHora(LocalDateTime.now().plusDays(1));
        partidaPrueba.setEstado(Partida.Estado.PROGRAMADA);

        partidaDTO = new PartidaDTO();
        partidaDTO.setTorneoId(1L);
        partidaDTO.setParticipanteAId(10L);
        partidaDTO.setParticipanteBId(20L);
        partidaDTO.setRonda("SEMIFINAL");
        partidaDTO.setFechaHora(LocalDateTime.now().plusDays(1));

        torneoDTO = new TorneoDTO();
        torneoDTO.setTorneoId(1L);
        torneoDTO.setNombre("Torneo Test");
        torneoDTO.setEstado("EN_CURSO");

        InscripcionDTO inscA = new InscripcionDTO();
        inscA.setEquipoId(10L);
        InscripcionDTO inscB = new InscripcionDTO();
        inscB.setEquipoId(20L);
        inscripcionesMock = List.of(inscA, inscB);
    }

    @Test
    @DisplayName("Debe retornar todas las partidas")
    public void shouldFindAllPartidas() {
        when(partidaRepository.findAll()).thenReturn(List.of(partidaPrueba));

        List<Partida> result = partidaService.findAll();

        assertThat(result).hasSize(1);
        verify(partidaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe encontrar una partida por su id")
    public void shouldFindPartidaById() {
        when(partidaRepository.findById(1L)).thenReturn(Optional.of(partidaPrueba));

        Partida result = partidaService.findById(1L);

        assertThat(result.getRonda()).isEqualTo("SEMIFINAL");
        verify(partidaRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando la partida no existe")
    public void shouldThrowWhenPartidaNotFound() {
        when(partidaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> partidaService.findById(999L))
                .isInstanceOf(PartidaException.class)
                .hasMessageContaining("999");
    }

    @Test
    @DisplayName("Debe crear una partida cuando el torneo esta EN_CURSO y los participantes inscritos")
    public void shouldSavePartida() {
        when(torneoClient.getTorneoById(1L)).thenReturn(torneoDTO);
        when(inscripcionClient.getInscripcionesByTorneo(1L)).thenReturn(inscripcionesMock);
        when(partidaRepository.findByTorneoIdAndParticipanteAIdAndParticipanteBIdAndRonda(
                anyLong(), anyLong(), anyLong(), anyString())).thenReturn(Optional.empty());
        when(partidaRepository.save(any(Partida.class))).thenReturn(partidaPrueba);

        Partida result = partidaService.save(partidaDTO);

        assertThat(result.getEstado()).isEqualTo(Partida.Estado.PROGRAMADA);
        verify(partidaRepository, times(1)).save(any(Partida.class));
    }

    @Test
    @DisplayName("Debe lanzar excepcion si el torneo no existe")
    public void shouldThrowWhenTorneoNoExiste() {
        when(torneoClient.getTorneoById(1L)).thenThrow(mock(FeignException.class));

        assertThatThrownBy(() -> partidaService.save(partidaDTO))
                .isInstanceOf(PartidaException.class)
                .hasMessageContaining("no existe");

        verify(partidaRepository, never()).save(any(Partida.class));
    }

    @Test
    @DisplayName("Debe lanzar excepcion si el torneo no esta EN_CURSO")
    public void shouldThrowWhenTorneoNoEnCurso() {
        torneoDTO.setEstado("BORRADOR");
        when(torneoClient.getTorneoById(1L)).thenReturn(torneoDTO);

        assertThatThrownBy(() -> partidaService.save(partidaDTO))
                .isInstanceOf(PartidaException.class)
                .hasMessageContaining("EN_CURSO");

        verify(partidaRepository, never()).save(any(Partida.class));
    }

    @Test
    @DisplayName("Debe lanzar excepcion si los dos participantes son el mismo")
    public void shouldThrowWhenParticipantesIguales() {
        when(torneoClient.getTorneoById(1L)).thenReturn(torneoDTO);
        partidaDTO.setParticipanteBId(10L);

        assertThatThrownBy(() -> partidaService.save(partidaDTO))
                .isInstanceOf(PartidaException.class)
                .hasMessageContaining("mismo");

        verify(partidaRepository, never()).save(any(Partida.class));
    }

    @Test
    @DisplayName("Debe cancelar una partida PROGRAMADA")
    public void shouldCancelarPartida() {
        when(partidaRepository.findById(1L)).thenReturn(Optional.of(partidaPrueba));
        when(partidaRepository.save(any(Partida.class))).thenAnswer(inv -> inv.getArgument(0));

        Partida result = partidaService.cancelar(1L);

        assertThat(result.getEstado()).isEqualTo(Partida.Estado.CANCELADA);
        verify(partidaRepository, times(1)).save(partidaPrueba);
    }

    @Test
    @DisplayName("Debe lanzar excepcion al cancelar una partida ya FINALIZADA")
    public void shouldThrowWhenCancelarPartidaFinalizada() {
        partidaPrueba.setEstado(Partida.Estado.FINALIZADA);
        when(partidaRepository.findById(1L)).thenReturn(Optional.of(partidaPrueba));

        assertThatThrownBy(() -> partidaService.cancelar(1L))
                .isInstanceOf(PartidaException.class)
                .hasMessageContaining("finalizada");

        verify(partidaRepository, never()).save(any(Partida.class));
    }
}