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
    @Mock private RankingControlService control;
    @Mock private com.esports.rankingservice.clients.InscripcionesClient inscripciones;

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
        when(rankingRepository.findByTorneoIdOrderByPuntosDescDiferenciaDesc(100L)).thenReturn(List.of(rankingPrueba));

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
    @DisplayName("Recalcular no duplica puntos, respeta ganador B y elimina resultados anulados")
    public void shouldRecalcularDesdeResultados() {
        var b=new Ranking(); b.setTorneoId(100L); b.setParticipanteId(20L); b.setPuntos(0); b.setVictorias(0); b.setDerrotas(0); b.setDiferencia(0); b.setEstado(Ranking.Estado.ACTIVO);
        var resultado=new ResultadoDTO(); resultado.setPartidaId(200L); resultado.setGanadorId(20L); resultado.setPuntajeA(1); resultado.setPuntajeB(3); resultado.setEstadoValidacion("VALIDADO");
        var partida=new PartidaDTO(); partida.setPartidaId(200L); partida.setParticipanteAId(10L); partida.setParticipanteBId(20L);
        when(rankingRepository.findByTorneoIdOrderByPuntosDescDiferenciaDesc(100L)).thenReturn(List.of(rankingPrueba,b));
        when(resultadoClient.getResultadosByTorneo(100L)).thenReturn(List.of(resultado)); when(partidaClient.getPartidaById(200L)).thenReturn(partida);
        rankingService.recalcularDesdeResultados(100L); var segunda=rankingService.recalcularDesdeResultados(100L);
        assertThat(segunda.getFirst().getParticipanteId()).isEqualTo(20L); assertThat(b.getPuntos()).isEqualTo(3); assertThat(b.getVictorias()).isEqualTo(1); assertThat(b.getDiferencia()).isEqualTo(2); assertThat(rankingPrueba.getDiferencia()).isEqualTo(-2);
        when(resultadoClient.getResultadosByTorneo(100L)).thenReturn(List.of()); rankingService.recalcularDesdeResultados(100L); assertThat(b.getPuntos()).isZero(); assertThat(b.getDiferencia()).isZero();
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
        when(rankingRepository.findByTorneoIdOrderByPuntosDescDiferenciaDesc(100L)).thenReturn(List.of(rankingPrueba));
        when(rankingRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        List<Ranking> result = rankingService.cerrarRanking(100L);

        assertThat(result).allMatch(r -> r.getEstado() == Ranking.Estado.CERRADO);
        verify(rankingRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("Debe lanzar excepcion al cerrar ranking de un torneo sin registros")
    public void shouldNotCerrarRankingWhenVacio() {
        when(rankingRepository.findByTorneoIdOrderByPuntosDescDiferenciaDesc(100L)).thenReturn(List.of());

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
