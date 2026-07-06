package com.esports.resultservice.services;

import com.esports.resultservice.clients.PartidaClient;
import com.esports.resultservice.exceptions.ResultadoException;
import com.esports.resultservice.models.Resultado;
import com.esports.resultservice.models.dtos.AnulacionDTO;
import com.esports.resultservice.models.dtos.PartidaDTO;
import com.esports.resultservice.models.dtos.ResultadoDTO;
import com.esports.resultservice.repositories.ResultadoRepository;
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
public class ResultadoServiceImplTest {

    @Mock
    private ResultadoRepository resultadoRepository;

    @Mock
    private PartidaClient partidaClient;

    @InjectMocks
    private ResultadoServiceImpl resultadoService;

    private Resultado resultadoPrueba;
    private ResultadoDTO resultadoDTO;
    private PartidaDTO partidaEnCurso;

    @BeforeEach
    public void setUp() {
        resultadoPrueba = new Resultado();
        resultadoPrueba.setResultadoId(1L);
        resultadoPrueba.setPartidaId(200L);
        resultadoPrueba.setGanadorId(10L);
        resultadoPrueba.setPuntajeA(3);
        resultadoPrueba.setPuntajeB(1);
        resultadoPrueba.setEstadoValidacion(Resultado.EstadoValidacion.PENDIENTE);

        resultadoDTO = new ResultadoDTO();
        resultadoDTO.setPartidaId(200L);
        resultadoDTO.setGanadorId(10L);
        resultadoDTO.setPuntajeA(3);
        resultadoDTO.setPuntajeB(1);

        partidaEnCurso = new PartidaDTO();
        partidaEnCurso.setPartidaId(200L);
        partidaEnCurso.setTorneoId(100L);
        partidaEnCurso.setParticipanteAId(10L);
        partidaEnCurso.setParticipanteBId(20L);
        partidaEnCurso.setEstado("EN_CURSO");
    }

    @Test
    @DisplayName("Debe retornar todos los resultados")
    public void shouldFindAllResultados() {
        when(resultadoRepository.findAll()).thenReturn(List.of(resultadoPrueba));

        List<Resultado> result = resultadoService.findAll();

        assertThat(result).hasSize(1);
        verify(resultadoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe encontrar un resultado por su id")
    public void shouldFindResultadoById() {
        when(resultadoRepository.findById(1L)).thenReturn(Optional.of(resultadoPrueba));

        Resultado result = resultadoService.findById(1L);

        assertThat(result.getGanadorId()).isEqualTo(10L);
        verify(resultadoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando el resultado no existe")
    public void shouldThrowWhenResultadoNotFound() {
        when(resultadoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resultadoService.findById(999L))
                .isInstanceOf(ResultadoException.class);
        verify(resultadoRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Debe registrar un resultado valido")
    public void shouldSaveResultado() {
        when(partidaClient.getPartidaById(200L)).thenReturn(partidaEnCurso);
        when(resultadoRepository.findByPartidaId(200L)).thenReturn(Optional.empty());
        when(resultadoRepository.save(any(Resultado.class))).thenAnswer(inv -> inv.getArgument(0));

        Resultado result = resultadoService.save(resultadoDTO);

        assertThat(result).isNotNull();
        assertThat(result.getEstadoValidacion()).isEqualTo(Resultado.EstadoValidacion.PENDIENTE);
        verify(resultadoRepository, times(1)).save(any(Resultado.class));
    }

    @Test
    @DisplayName("Debe lanzar excepcion al registrar resultado de una partida inexistente")
    public void shouldNotSaveWhenPartidaNotExists() {
        when(partidaClient.getPartidaById(200L)).thenThrow(mock(feign.FeignException.class));

        assertThatThrownBy(() -> resultadoService.save(resultadoDTO))
                .isInstanceOf(ResultadoException.class);
        verify(resultadoRepository, never()).save(any(Resultado.class));
    }

    @Test
    @DisplayName("Debe lanzar excepcion al registrar resultado de una partida cancelada")
    public void shouldNotSaveWhenPartidaCancelada() {
        partidaEnCurso.setEstado("CANCELADA");
        when(partidaClient.getPartidaById(200L)).thenReturn(partidaEnCurso);

        assertThatThrownBy(() -> resultadoService.save(resultadoDTO))
                .isInstanceOf(ResultadoException.class);
        verify(resultadoRepository, never()).save(any(Resultado.class));
    }

    @Test
    @DisplayName("Debe lanzar excepcion al registrar resultado de una partida no iniciada")
    public void shouldNotSaveWhenPartidaProgramada() {
        partidaEnCurso.setEstado("PROGRAMADA");
        when(partidaClient.getPartidaById(200L)).thenReturn(partidaEnCurso);

        assertThatThrownBy(() -> resultadoService.save(resultadoDTO))
                .isInstanceOf(ResultadoException.class);
        verify(resultadoRepository, never()).save(any(Resultado.class));
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando ya existe un resultado para la partida")
    public void shouldNotSaveWhenResultadoYaExiste() {
        when(partidaClient.getPartidaById(200L)).thenReturn(partidaEnCurso);
        when(resultadoRepository.findByPartidaId(200L)).thenReturn(Optional.of(resultadoPrueba));

        assertThatThrownBy(() -> resultadoService.save(resultadoDTO))
                .isInstanceOf(ResultadoException.class);
        verify(resultadoRepository, never()).save(any(Resultado.class));
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando el ganador no es participante de la partida")
    public void shouldNotSaveWhenGanadorNoParticipa() {
        resultadoDTO.setGanadorId(999L);
        when(partidaClient.getPartidaById(200L)).thenReturn(partidaEnCurso);
        when(resultadoRepository.findByPartidaId(200L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resultadoService.save(resultadoDTO))
                .isInstanceOf(ResultadoException.class);
        verify(resultadoRepository, never()).save(any(Resultado.class));
    }

    @Test
    @DisplayName("Debe actualizar un resultado pendiente")
    public void shouldUpdateResultado() {
        ResultadoDTO cambios = new ResultadoDTO();
        cambios.setGanadorId(20L);
        cambios.setPuntajeA(2);
        cambios.setPuntajeB(3);

        when(resultadoRepository.findById(1L)).thenReturn(Optional.of(resultadoPrueba));
        when(resultadoRepository.save(any(Resultado.class))).thenAnswer(inv -> inv.getArgument(0));

        Resultado result = resultadoService.update(1L, cambios);

        assertThat(result.getGanadorId()).isEqualTo(20L);
        verify(resultadoRepository, times(1)).save(resultadoPrueba);
    }

    @Test
    @DisplayName("Debe lanzar excepcion al actualizar un resultado ya validado")
    public void shouldNotUpdateWhenValidado() {
        resultadoPrueba.setEstadoValidacion(Resultado.EstadoValidacion.VALIDADO);
        when(resultadoRepository.findById(1L)).thenReturn(Optional.of(resultadoPrueba));

        assertThatThrownBy(() -> resultadoService.update(1L, resultadoDTO))
                .isInstanceOf(ResultadoException.class);
        verify(resultadoRepository, never()).save(any(Resultado.class));
    }

    @Test
    @DisplayName("Debe validar un resultado pendiente")
    public void shouldValidarResultado() {
        when(resultadoRepository.findById(1L)).thenReturn(Optional.of(resultadoPrueba));
        when(resultadoRepository.save(any(Resultado.class))).thenAnswer(inv -> inv.getArgument(0));

        Resultado result = resultadoService.validar(1L);

        assertThat(result.getEstadoValidacion()).isEqualTo(Resultado.EstadoValidacion.VALIDADO);
        verify(resultadoRepository, times(1)).save(resultadoPrueba);
    }

    @Test
    @DisplayName("Debe lanzar excepcion al validar un resultado que no esta pendiente")
    public void shouldNotValidarWhenNotPendiente() {
        resultadoPrueba.setEstadoValidacion(Resultado.EstadoValidacion.ANULADO);
        when(resultadoRepository.findById(1L)).thenReturn(Optional.of(resultadoPrueba));

        assertThatThrownBy(() -> resultadoService.validar(1L))
                .isInstanceOf(ResultadoException.class);
        verify(resultadoRepository, never()).save(any(Resultado.class));
    }

    @Test
    @DisplayName("Debe anular un resultado con justificacion")
    public void shouldAnularResultado() {
        AnulacionDTO anulacion = new AnulacionDTO();
        anulacion.setJustificacion("Error de arbitraje");

        when(resultadoRepository.findById(1L)).thenReturn(Optional.of(resultadoPrueba));
        when(resultadoRepository.save(any(Resultado.class))).thenAnswer(inv -> inv.getArgument(0));

        Resultado result = resultadoService.anular(1L, anulacion);

        assertThat(result.getEstadoValidacion()).isEqualTo(Resultado.EstadoValidacion.ANULADO);
        assertThat(result.getJustificacionAnulacion()).isEqualTo("Error de arbitraje");
        verify(resultadoRepository, times(1)).save(resultadoPrueba);
    }

    @Test
    @DisplayName("Debe lanzar excepcion al anular un resultado ya anulado")
    public void shouldNotAnularWhenYaAnulado() {
        resultadoPrueba.setEstadoValidacion(Resultado.EstadoValidacion.ANULADO);
        AnulacionDTO anulacion = new AnulacionDTO();
        anulacion.setJustificacion("Intento repetido");

        when(resultadoRepository.findById(1L)).thenReturn(Optional.of(resultadoPrueba));

        assertThatThrownBy(() -> resultadoService.anular(1L, anulacion))
                .isInstanceOf(ResultadoException.class);
        verify(resultadoRepository, never()).save(any(Resultado.class));
    }
}
