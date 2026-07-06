package com.esports.rankingservice.services;

import com.esports.rankingservice.clients.PartidaClient;
import com.esports.rankingservice.clients.ResultadoClient;
import com.esports.rankingservice.exceptions.RankingException;
import com.esports.rankingservice.models.Ranking;
import com.esports.rankingservice.models.dtos.ActualizarPuntosDTO;
import com.esports.rankingservice.models.dtos.PartidaDTO;
import com.esports.rankingservice.models.dtos.RankingDTO;
import com.esports.rankingservice.models.dtos.ResultadoDTO;
import com.esports.rankingservice.repositories.RankingRepository;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RankingServiceImplTest {

    @Mock
    private RankingRepository rankingRepository;

    @Mock
    private ResultadoClient resultadoClient;

    @Mock
    private PartidaClient partidaClient;

    @InjectMocks
    private RankingServiceImpl rankingService;

    private Ranking rankingPrueba;
    private RankingDTO rankingDTO;

    @BeforeEach
    public void setUp() {
        rankingPrueba = new Ranking();
        rankingPrueba.setRankingId(1L);
        rankingPrueba.setTorneoId(100L);
        rankingPrueba.setParticipanteId(10L);
        rankingPrueba.setPuntos(0);
        rankingPrueba.setVictorias(0);
        rankingPrueba.setDerrotas(0);
        rankingPrueba.setDiferencia(0);
        rankingPrueba.setPosicion(0);
        rankingPrueba.setEstado(Ranking.Estado.ACTIVO);

        rankingDTO = new RankingDTO();
        rankingDTO.setTorneoId(100L);
        rankingDTO.setParticipanteId(10L);
    }

    @Test
    @DisplayName("Debe retornar todos los rankings")
    public void shouldFindAllRankings() {
        when(rankingRepository.findAll()).thenReturn(List.of(rankingPrueba));

        List<Ranking> result = rankingService.findAll();

        assertThat(result).hasSize(1);
        verify(rankingRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe encontrar un ranking por su id")
    public void shouldFindRankingById() {
        when(rankingRepository.findById(1L)).thenReturn(Optional.of(rankingPrueba));

        Ranking result = rankingService.findById(1L);

        assertThat(result.getParticipanteId()).isEqualTo(10L);
        verify(rankingRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando el ranking no existe")
    public void shouldThrowWhenRankingNotFound() {
        when(rankingRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> rankingService.findById(999L))
                .isInstanceOf(RankingException.class);
        verify(rankingRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Debe crear un registro de ranking nuevo")
    public void shouldSaveRanking() {
        when(rankingRepository.findByTorneoIdAndParticipanteId(100L, 10L)).thenReturn(Optional.empty());
        when(rankingRepository.save(any(Ranking.class))).thenAnswer(inv -> inv.getArgument(0));

        Ranking result = rankingService.save(rankingDTO);

        assertThat(result).isNotNull();
        assertThat(result.getEstado()).isEqualTo(Ranking.Estado.ACTIVO);
        verify(rankingRepository, times(1)).save(any(Ranking.class));
    }

    @Test
    @DisplayName("Debe lanzar excepcion al crear un ranking duplicado")
    public void shouldNotSaveWhenRankingDuplicado() {
        when(rankingRepository.findByTorneoIdAndParticipanteId(100L, 10L)).thenReturn(Optional.of(rankingPrueba));

        assertThatThrownBy(() -> rankingService.save(rankingDTO))
                .isInstanceOf(RankingException.class);
        verify(rankingRepository, never()).save(any(Ranking.class));
    }

    @Test
    @DisplayName("Debe actualizar los puntos de un ranking activo")
    public void shouldActualizarPuntos() {
        ActualizarPuntosDTO puntosDTO = new ActualizarPuntosDTO();
        puntosDTO.setPuntos(3);
        puntosDTO.setVictorias(1);
        puntosDTO.setDerrotas(0);
        puntosDTO.setDiferencia(2);

        when(rankingRepository.findById(1L)).thenReturn(Optional.of(rankingPrueba));
        when(rankingRepository.save(any(Ranking.class))).thenAnswer(inv -> inv.getArgument(0));
        when(rankingRepository.findByTorneoIdOrderByPuntosDesc(100L)).thenReturn(List.of(rankingPrueba));

        Ranking result = rankingService.actualizarPuntos(1L, puntosDTO);

        assertThat(result.getPuntos()).isEqualTo(3);
        assertThat(result.getVictorias()).isEqualTo(1);
        verify(rankingRepository, atLeastOnce()).save(any(Ranking.class));
    }

    @Test
    @DisplayName("Debe lanzar excepcion al actualizar puntos de un ranking cerrado")
    public void shouldNotActualizarPuntosWhenCerrado() {
        rankingPrueba.setEstado(Ranking.Estado.CERRADO);
        ActualizarPuntosDTO puntosDTO = new ActualizarPuntosDTO();
        puntosDTO.setPuntos(3);
        puntosDTO.setVictorias(1);
        puntosDTO.setDerrotas(0);
        puntosDTO.setDiferencia(2);

        when(rankingRepository.findById(1L)).thenReturn(Optional.of(rankingPrueba));

        assertThatThrownBy(() -> rankingService.actualizarPuntos(1L, puntosDTO))
                .isInstanceOf(RankingException.class);
        verify(rankingRepository, never()).save(any(Ranking.class));
    }

    @Test
    @DisplayName("Debe recalcular el ranking desde resultados validados")
    public void shouldRecalcularDesdeResultados() {
        ResultadoDTO resultadoValidado = new ResultadoDTO();
        resultadoValidado.setPartidaId(200L);
        resultadoValidado.setGanadorId(10L);
        resultadoValidado.setPuntajeA(3);
        resultadoValidado.setPuntajeB(1);
        resultadoValidado.setEstadoValidacion("VALIDADO");

        PartidaDTO partida = new PartidaDTO();
        partida.setPartidaId(200L);
        partida.setParticipanteAId(10L);
        partida.setParticipanteBId(20L);

        when(resultadoClient.getResultadosByTorneo(100L)).thenReturn(List.of(resultadoValidado));
        when(partidaClient.getPartidaById(200L)).thenReturn(partida);
        when(rankingRepository.findByTorneoIdAndParticipanteId(100L, 10L)).thenReturn(Optional.of(rankingPrueba));
        when(rankingRepository.findByTorneoIdAndParticipanteId(100L, 20L)).thenReturn(Optional.empty());
        when(rankingRepository.save(any(Ranking.class))).thenAnswer(inv -> inv.getArgument(0));
        when(rankingRepository.findByTorneoIdOrderByPuntosDesc(100L)).thenReturn(List.of(rankingPrueba));
        when(rankingRepository.findByTorneoIdOrderByPuntosDescDiferenciaDesc(100L)).thenReturn(List.of(rankingPrueba));

        List<Ranking> result = rankingService.recalcularDesdeResultados(100L);

        assertThat(result).hasSize(1);
        verify(resultadoClient, times(1)).getResultadosByTorneo(100L);
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando falla la consulta de resultados")
    public void shouldThrowWhenResultadosClientFalla() {
        when(resultadoClient.getResultadosByTorneo(100L)).thenThrow(mock(feign.FeignException.class));

        assertThatThrownBy(() -> rankingService.recalcularDesdeResultados(100L))
                .isInstanceOf(RankingException.class);
    }

    @Test
    @DisplayName("Debe cerrar el ranking de un torneo con registros")
    public void shouldCerrarRanking() {
        when(rankingRepository.findByTorneoIdOrderByPuntosDesc(100L)).thenReturn(List.of(rankingPrueba));
        when(rankingRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        List<Ranking> result = rankingService.cerrarRanking(100L);

        assertThat(result).allMatch(r -> r.getEstado() == Ranking.Estado.CERRADO);
        verify(rankingRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("Debe lanzar excepcion al cerrar ranking de un torneo sin registros")
    public void shouldNotCerrarRankingWhenVacio() {
        when(rankingRepository.findByTorneoIdOrderByPuntosDesc(100L)).thenReturn(List.of());

        assertThatThrownBy(() -> rankingService.cerrarRanking(100L))
                .isInstanceOf(RankingException.class);
        verify(rankingRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("Debe listar el ranking de un torneo")
    public void shouldFindByTorneoId() {
        when(rankingRepository.findByTorneoIdOrderByPuntosDescDiferenciaDesc(100L)).thenReturn(List.of(rankingPrueba));

        List<Ranking> result = rankingService.findByTorneoId(100L);

        assertThat(result).hasSize(1);
        verify(rankingRepository, times(1)).findByTorneoIdOrderByPuntosDescDiferenciaDesc(100L);
    }

    @Test
    @DisplayName("Debe buscar la posicion de un participante en un torneo")
    public void shouldFindByTorneoIdAndParticipanteId() {
        when(rankingRepository.findByTorneoIdAndParticipanteId(100L, 10L)).thenReturn(Optional.of(rankingPrueba));

        Ranking result = rankingService.findByTorneoIdAndParticipanteId(100L, 10L);

        assertThat(result.getParticipanteId()).isEqualTo(10L);
        verify(rankingRepository, times(1)).findByTorneoIdAndParticipanteId(100L, 10L);
    }
}
