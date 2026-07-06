package com.esports.registrationservice.services;

import com.esports.registrationservice.clients.EquipoClient;
import com.esports.registrationservice.clients.SancionClient;
import com.esports.registrationservice.clients.TorneoClient;
import com.esports.registrationservice.clients.UsuarioClient;
import com.esports.registrationservice.exceptions.InscripcionException;
import com.esports.registrationservice.models.Inscripcion;
import com.esports.registrationservice.models.dtos.EquipoDTO;
import com.esports.registrationservice.models.dtos.InscripcionDTO;
import com.esports.registrationservice.models.dtos.TorneoDTO;
import com.esports.registrationservice.models.dtos.UsuarioDTO;
import com.esports.registrationservice.repositories.InscripcionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InscripcionServiceImplTest {

    @Mock
    private InscripcionRepository inscripcionRepository;

    @Mock
    private TorneoClient torneoClient;

    @Mock
    private UsuarioClient usuarioClient;

    @Mock
    private EquipoClient equipoClient;

    @Mock
    private SancionClient sancionClient;

    @InjectMocks
    private InscripcionServiceImpl inscripcionService;

    private Inscripcion inscripcionPrueba;
    private InscripcionDTO inscripcionDTOEquipo;
    private InscripcionDTO inscripcionDTOIndividual;
    private TorneoDTO torneoAbierto;

    @BeforeEach
    public void setUp() {
        inscripcionPrueba = new Inscripcion();
        inscripcionPrueba.setInscripcionId(1L);
        inscripcionPrueba.setTorneoId(100L);
        inscripcionPrueba.setEquipoId(50L);
        inscripcionPrueba.setTipoParticipante(Inscripcion.TipoParticipante.EQUIPO);
        inscripcionPrueba.setEstado(Inscripcion.Estado.CONFIRMADA);
        inscripcionPrueba.setFechaInscripcion(LocalDateTime.now());

        inscripcionDTOEquipo = new InscripcionDTO();
        inscripcionDTOEquipo.setTorneoId(100L);
        inscripcionDTOEquipo.setEquipoId(50L);
        inscripcionDTOEquipo.setTipoParticipante(Inscripcion.TipoParticipante.EQUIPO);

        inscripcionDTOIndividual = new InscripcionDTO();
        inscripcionDTOIndividual.setTorneoId(100L);
        inscripcionDTOIndividual.setJugadorId(20L);
        inscripcionDTOIndividual.setTipoParticipante(Inscripcion.TipoParticipante.INDIVIDUAL);

        torneoAbierto = new TorneoDTO();
        torneoAbierto.setTorneoId(100L);
        torneoAbierto.setEstado("ABIERTO");
        torneoAbierto.setCupoMaximo(16);
        torneoAbierto.setCupoActual(0);
        torneoAbierto.setFechaCierreInscripcion(LocalDateTime.now().plusDays(5));
    }

    @Test
    @DisplayName("Debe retornar todas las inscripciones")
    public void shouldFindAllInscripciones() {
        when(inscripcionRepository.findAll()).thenReturn(List.of(inscripcionPrueba));

        List<Inscripcion> result = inscripcionService.findAll();

        assertThat(result).hasSize(1);
        verify(inscripcionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe encontrar una inscripcion por su id")
    public void shouldFindInscripcionById() {
        when(inscripcionRepository.findById(1L)).thenReturn(Optional.of(inscripcionPrueba));

        Inscripcion result = inscripcionService.findById(1L);

        assertThat(result.getTorneoId()).isEqualTo(100L);
        verify(inscripcionRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando la inscripcion no existe")
    public void shouldThrowWhenInscripcionNotFound() {
        when(inscripcionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> inscripcionService.findById(999L))
                .isInstanceOf(InscripcionException.class);
        verify(inscripcionRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Debe crear una inscripcion de equipo cuando todo es valido")
    public void shouldSaveInscripcionEquipo() {
        EquipoDTO equipo = new EquipoDTO();
        equipo.setEquipoId(50L);
        equipo.setNombre("Los Halcones");
        equipo.setEstado(true);

        when(torneoClient.getTorneoById(100L)).thenReturn(torneoAbierto);
        when(inscripcionRepository.countByTorneoIdAndEstado(100L, Inscripcion.Estado.CONFIRMADA)).thenReturn(0L);
        when(equipoClient.getEquipoById(50L)).thenReturn(equipo);
        when(sancionClient.verificarSancion(null, 50L)).thenReturn(Map.of("sancionado", false));
        when(inscripcionRepository.findByTorneoIdAndEquipoId(100L, 50L)).thenReturn(Optional.empty());
        when(inscripcionRepository.save(any(Inscripcion.class))).thenAnswer(inv -> inv.getArgument(0));

        Inscripcion result = inscripcionService.save(inscripcionDTOEquipo);

        assertThat(result).isNotNull();
        assertThat(result.getEstado()).isEqualTo(Inscripcion.Estado.CONFIRMADA);
        verify(inscripcionRepository, times(1)).save(any(Inscripcion.class));
    }

    @Test
    @DisplayName("Debe lanzar excepcion al inscribir en un torneo que no existe")
    public void shouldNotSaveWhenTorneoNotExists() {
        when(torneoClient.getTorneoById(100L)).thenThrow(mock(feign.FeignException.class));

        assertThatThrownBy(() -> inscripcionService.save(inscripcionDTOEquipo))
                .isInstanceOf(InscripcionException.class);
        verify(inscripcionRepository, never()).save(any(Inscripcion.class));
    }

    @Test
    @DisplayName("Debe lanzar excepcion al inscribir en un torneo no abierto")
    public void shouldNotSaveWhenTorneoNotAbierto() {
        TorneoDTO torneoCerrado = new TorneoDTO();
        torneoCerrado.setTorneoId(100L);
        torneoCerrado.setEstado("FINALIZADO");

        when(torneoClient.getTorneoById(100L)).thenReturn(torneoCerrado);

        assertThatThrownBy(() -> inscripcionService.save(inscripcionDTOEquipo))
                .isInstanceOf(InscripcionException.class);
        verify(inscripcionRepository, never()).save(any(Inscripcion.class));
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando el torneo alcanzo el cupo maximo")
    public void shouldNotSaveWhenCupoLleno() {
        when(torneoClient.getTorneoById(100L)).thenReturn(torneoAbierto);
        when(inscripcionRepository.countByTorneoIdAndEstado(100L, Inscripcion.Estado.CONFIRMADA)).thenReturn(16L);

        assertThatThrownBy(() -> inscripcionService.save(inscripcionDTOEquipo))
                .isInstanceOf(InscripcionException.class);
        verify(inscripcionRepository, never()).save(any(Inscripcion.class));
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando el equipo tiene sancion activa")
    public void shouldNotSaveWhenEquipoSancionado() {
        EquipoDTO equipo = new EquipoDTO();
        equipo.setEquipoId(50L);
        equipo.setEstado(true);

        when(torneoClient.getTorneoById(100L)).thenReturn(torneoAbierto);
        when(inscripcionRepository.countByTorneoIdAndEstado(100L, Inscripcion.Estado.CONFIRMADA)).thenReturn(0L);
        when(equipoClient.getEquipoById(50L)).thenReturn(equipo);
        when(sancionClient.verificarSancion(null, 50L)).thenReturn(Map.of("sancionado", true));

        assertThatThrownBy(() -> inscripcionService.save(inscripcionDTOEquipo))
                .isInstanceOf(InscripcionException.class);
        verify(inscripcionRepository, never()).save(any(Inscripcion.class));
    }

    @Test
    @DisplayName("Debe crear una inscripcion individual cuando todo es valido")
    public void shouldSaveInscripcionIndividual() {
        UsuarioDTO usuario = new UsuarioDTO();
        usuario.setUsuarioId(20L);
        usuario.setNickname("ProGamer");
        usuario.setEstado("ACTIVO");

        when(torneoClient.getTorneoById(100L)).thenReturn(torneoAbierto);
        when(inscripcionRepository.countByTorneoIdAndEstado(100L, Inscripcion.Estado.CONFIRMADA)).thenReturn(0L);
        when(usuarioClient.getUsuarioById(20L)).thenReturn(usuario);
        when(sancionClient.verificarSancion(20L, null)).thenReturn(Map.of("sancionado", false));
        when(inscripcionRepository.findByTorneoIdAndJugadorId(100L, 20L)).thenReturn(Optional.empty());
        when(inscripcionRepository.save(any(Inscripcion.class))).thenAnswer(inv -> inv.getArgument(0));

        Inscripcion result = inscripcionService.save(inscripcionDTOIndividual);

        assertThat(result).isNotNull();
        assertThat(result.getTipoParticipante()).isEqualTo(Inscripcion.TipoParticipante.INDIVIDUAL);
        verify(inscripcionRepository, times(1)).save(any(Inscripcion.class));
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando el jugador ya esta inscrito en el torneo")
    public void shouldNotSaveWhenJugadorYaInscrito() {
        UsuarioDTO usuario = new UsuarioDTO();
        usuario.setUsuarioId(20L);
        usuario.setEstado("ACTIVO");

        when(torneoClient.getTorneoById(100L)).thenReturn(torneoAbierto);
        when(inscripcionRepository.countByTorneoIdAndEstado(100L, Inscripcion.Estado.CONFIRMADA)).thenReturn(0L);
        when(usuarioClient.getUsuarioById(20L)).thenReturn(usuario);
        when(sancionClient.verificarSancion(20L, null)).thenReturn(Map.of("sancionado", false));
        when(inscripcionRepository.findByTorneoIdAndJugadorId(100L, 20L)).thenReturn(Optional.of(inscripcionPrueba));

        assertThatThrownBy(() -> inscripcionService.save(inscripcionDTOIndividual))
                .isInstanceOf(InscripcionException.class);
        verify(inscripcionRepository, never()).save(any(Inscripcion.class));
    }

    @Test
    @DisplayName("Debe cancelar una inscripcion existente")
    public void shouldCancelInscripcion() {
        when(inscripcionRepository.findById(1L)).thenReturn(Optional.of(inscripcionPrueba));
        when(inscripcionRepository.save(any(Inscripcion.class))).thenAnswer(inv -> inv.getArgument(0));

        Inscripcion result = inscripcionService.cancelar(1L);

        assertThat(result.getEstado()).isEqualTo(Inscripcion.Estado.CANCELADA);
        verify(inscripcionRepository, times(1)).save(inscripcionPrueba);
    }

    @Test
    @DisplayName("Debe lanzar excepcion al cancelar una inscripcion ya cancelada")
    public void shouldNotCancelWhenAlreadyCancelada() {
        inscripcionPrueba.setEstado(Inscripcion.Estado.CANCELADA);
        when(inscripcionRepository.findById(1L)).thenReturn(Optional.of(inscripcionPrueba));

        assertThatThrownBy(() -> inscripcionService.cancelar(1L))
                .isInstanceOf(InscripcionException.class);
        verify(inscripcionRepository, never()).save(any(Inscripcion.class));
    }

    @Test
    @DisplayName("Debe actualizar el estado de una inscripcion")
    public void shouldUpdateEstado() {
        when(inscripcionRepository.findById(1L)).thenReturn(Optional.of(inscripcionPrueba));
        when(inscripcionRepository.save(any(Inscripcion.class))).thenAnswer(inv -> inv.getArgument(0));

        Inscripcion result = inscripcionService.actualizarEstado(1L, "PENDIENTE");

        assertThat(result.getEstado()).isEqualTo(Inscripcion.Estado.PENDIENTE);
        verify(inscripcionRepository, times(1)).save(inscripcionPrueba);
    }

    @Test
    @DisplayName("Debe listar las inscripciones de un torneo")
    public void shouldFindByTorneoId() {
        when(inscripcionRepository.findByTorneoId(100L)).thenReturn(List.of(inscripcionPrueba));

        List<Inscripcion> result = inscripcionService.findByTorneoId(100L);

        assertThat(result).hasSize(1);
        verify(inscripcionRepository, times(1)).findByTorneoId(100L);
    }
}
