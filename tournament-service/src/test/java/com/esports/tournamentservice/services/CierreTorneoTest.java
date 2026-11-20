package com.esports.tournamentservice.services;

import com.esports.common.ArenaException;
import com.esports.tournamentservice.clients.*;
import com.esports.tournamentservice.models.*;
import com.esports.tournamentservice.repositories.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CierreTorneoTest {
    @Mock TorneoRepository torneos;
    @Mock PremioRepository premios;
    @Mock EntregaRepository entregas;
    @Mock CierreRankingClient rankings;
    @Mock CierrePartidasClient partidas;
    @Mock CierreResultadosClient resultados;
    @Mock CierreEquipoClient equipos;
    @InjectMocks CierreTorneo cierre;

    Torneo torneo() {
        var t = new Torneo();
        t.setTorneoId(1L); t.setNombre("Arena"); t.setEstado("EN_CURSO"); t.setModalidad("Duos");
        when(torneos.lock(1L)).thenReturn(Optional.of(t));
        return t;
    }

    @Test
    void cerrarDosVecesNoDuplicaPremiosNiEntregas() {
        var t = torneo();
        var premio = new Premio();
        premio.setTorneoId(1L); premio.setPosicion(1); premio.setDescripcion("Copa");
        when(partidas.listar(1L)).thenReturn(List.of(new CierrePartidasClient.Partida(10L, "FINALIZADA")));
        when(resultados.listar(1L)).thenReturn(List.of(new CierreResultadosClient.Resultado(10L, "VALIDADO")));
        when(rankings.recalcular(1L)).thenReturn(List.of(new CierreRankingClient.Posicion(99L, 1)));
        when(premios.findByTorneoIdOrderByPosicionAsc(1L)).thenReturn(List.of(premio));
        when(equipos.miembros(99L)).thenReturn(List.of(new CierreEquipoClient.Miembro(101L), new CierreEquipoClient.Miembro(102L)));
        when(torneos.save(t)).thenReturn(t);

        cierre.finalizar(1L);
        cierre.finalizar(1L);

        assertThat(t.getEstado()).isEqualTo("FINALIZADO");
        assertThat(premio.getParticipanteId()).isEqualTo(99L);
        verify(rankings, times(1)).recalcular(1L);
        verify(premios, times(1)).save(premio);
        var captor = ArgumentCaptor.forClass(EntregaPendiente.class);
        verify(entregas, times(2)).save(captor.capture());
        assertThat(captor.getAllValues()).extracting(EntregaPendiente::getId)
                .containsExactly("PREMIO:1:1:101", "PREMIO:1:1:102");
    }

    @Test
    void noCierraConPartidasProgramadas() {
        torneo();
        when(partidas.listar(1L)).thenReturn(List.of(new CierrePartidasClient.Partida(10L, "PROGRAMADA")));
        assertThatThrownBy(() -> cierre.finalizar(1L)).isInstanceOfSatisfying(ArenaException.class,
                e -> assertThat(e.status()).isEqualTo(409));
        verifyNoInteractions(rankings, premios, entregas);
    }

    @Test
    void noCierraConResultadosSinValidar() {
        torneo();
        when(partidas.listar(1L)).thenReturn(List.of(new CierrePartidasClient.Partida(10L, "FINALIZADA")));
        when(resultados.listar(1L)).thenReturn(List.of(new CierreResultadosClient.Resultado(10L, "PENDIENTE")));
        assertThatThrownBy(() -> cierre.finalizar(1L)).isInstanceOf(ArenaException.class);
        verifyNoInteractions(rankings, premios, entregas);
    }
}
