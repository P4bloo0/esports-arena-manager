package com.esports.sanctionservice.services;

import com.esports.sanctionservice.exceptions.SancionException;
import com.esports.sanctionservice.models.Sancion;
import com.esports.sanctionservice.models.dtos.SancionDTO;
import com.esports.sanctionservice.repositories.SancionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SancionServiceImplTest {

    @Mock
    private SancionRepository sancionRepository;

    @InjectMocks
    private SancionServiceImpl sancionService;

    private Sancion sancionPrueba;
    private SancionDTO sancionDTO;

    @BeforeEach
    public void setUp() {
        sancionPrueba = new Sancion();
        sancionPrueba.setSancionId(1L);
        sancionPrueba.setUsuarioId(10L);
        sancionPrueba.setMotivo("Conducta toxica en partida");
        sancionPrueba.setFechaInicio(LocalDateTime.now());
        sancionPrueba.setFechaFin(LocalDateTime.now().plusDays(7));
        sancionPrueba.setEstado("ACTIVA");
        sancionPrueba.setSeveridad("SUSPENSION");

        sancionDTO = new SancionDTO();
        sancionDTO.setUsuarioId(10L);
        sancionDTO.setEquipoId(null);
        sancionDTO.setMotivo("Conducta toxica en partida");
        sancionDTO.setFechaInicio(LocalDateTime.now());
        sancionDTO.setFechaFin(LocalDateTime.now().plusDays(7));
        sancionDTO.setSeveridad("SUSPENSION");
    }

    @Test
    @DisplayName("Debe retornar todas las sanciones")
    public void shouldFindAllSanciones() {
        when(sancionRepository.findAll()).thenReturn(List.of(sancionPrueba));

        List<Sancion> result = sancionService.findAll();

        assertThat(result).hasSize(1);
        verify(sancionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe encontrar una sancion por su id")
    public void shouldFindSancionById() {
        when(sancionRepository.findById(1L)).thenReturn(Optional.of(sancionPrueba));

        Sancion result = sancionService.findById(1L);

        assertThat(result.getMotivo()).isEqualTo("Conducta toxica en partida");
        verify(sancionRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando la sancion no existe")
    public void shouldThrowWhenSancionNotFound() {
        when(sancionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sancionService.findById(999L))
                .isInstanceOf(SancionException.class)
                .hasMessageContaining("999");
    }

    @Test
    @DisplayName("Debe guardar una sancion valida")
    public void shouldSaveSancion() {
        when(sancionRepository.save(any(Sancion.class))).thenReturn(sancionPrueba);

        Sancion result = sancionService.save(sancionDTO);

        assertThat(result.getEstado()).isEqualTo("ACTIVA");
        verify(sancionRepository, times(1)).save(any(Sancion.class));
    }

    @Test
    @DisplayName("Debe lanzar excepcion si la sancion no tiene usuario ni equipo")
    public void shouldThrowWhenSinDestinatario() {
        SancionDTO dtoInvalido = new SancionDTO();
        dtoInvalido.setUsuarioId(null);
        dtoInvalido.setEquipoId(null);
        dtoInvalido.setMotivo("Motivo");
        dtoInvalido.setFechaInicio(LocalDateTime.now());
        dtoInvalido.setFechaFin(LocalDateTime.now().plusDays(1));
        dtoInvalido.setSeveridad("ADVERTENCIA");

        assertThatThrownBy(() -> sancionService.save(dtoInvalido))
                .isInstanceOf(SancionException.class)
                .hasMessageContaining("al menos un usuario o equipo");

        verify(sancionRepository, never()).save(any(Sancion.class));
    }

    @Test
    @DisplayName("Debe lanzar excepcion si fecha fin es anterior a fecha inicio")
    public void shouldThrowWhenFechaFinAnterior() {
        SancionDTO dtoFechaInvalida = new SancionDTO();
        dtoFechaInvalida.setUsuarioId(10L);
        dtoFechaInvalida.setMotivo("Motivo");
        dtoFechaInvalida.setFechaInicio(LocalDateTime.now().plusDays(5));
        dtoFechaInvalida.setFechaFin(LocalDateTime.now());
        dtoFechaInvalida.setSeveridad("ADVERTENCIA");

        assertThatThrownBy(() -> sancionService.save(dtoFechaInvalida))
                .isInstanceOf(SancionException.class)
                .hasMessageContaining("posterior a la fecha de inicio");

        verify(sancionRepository, never()).save(any(Sancion.class));
    }

    @Test
    @DisplayName("Debe cerrar una sancion activa")
    public void shouldCerrarSancion() {
        when(sancionRepository.findById(1L)).thenReturn(Optional.of(sancionPrueba));
        when(sancionRepository.save(any(Sancion.class))).thenAnswer(inv -> inv.getArgument(0));

        Sancion result = sancionService.cerrar(1L);

        assertThat(result.getEstado()).isEqualTo("CERRADA");
        verify(sancionRepository, times(1)).save(sancionPrueba);
    }

    @Test
    @DisplayName("Debe lanzar excepcion al modificar una sancion cerrada")
    public void shouldThrowWhenUpdateSancionCerrada() {
        sancionPrueba.setEstado("CERRADA");
        when(sancionRepository.findById(1L)).thenReturn(Optional.of(sancionPrueba));

        assertThatThrownBy(() -> sancionService.update(1L, sancionDTO))
                .isInstanceOf(SancionException.class)
                .hasMessageContaining("cerrada");

        verify(sancionRepository, never()).save(any(Sancion.class));
    }

    @Test
    @DisplayName("Debe retornar true si el usuario tiene sancion activa")
    public void shouldReturnTrueWhenTieneSancionActiva() {
        when(sancionRepository.findByUsuarioIdAndEstado(10L, "ACTIVA"))
                .thenReturn(List.of(sancionPrueba));

        boolean result = sancionService.tieneSancionActiva(10L, null);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Debe retornar false si no tiene sanciones activas")
    public void shouldReturnFalseWhenNoSancionActiva() {
        when(sancionRepository.findByUsuarioIdAndEstado(10L, "ACTIVA"))
                .thenReturn(Collections.emptyList());

        boolean result = sancionService.tieneSancionActiva(10L, null);

        assertThat(result).isFalse();
    }
}