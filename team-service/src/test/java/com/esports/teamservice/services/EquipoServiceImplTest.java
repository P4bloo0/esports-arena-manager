package com.esports.teamservice.services;

import com.esports.teamservice.clients.JuegoClient;
import com.esports.teamservice.clients.UsuarioClient;
import com.esports.teamservice.exceptions.EquipoException;
import com.esports.teamservice.models.Equipo;
import com.esports.teamservice.models.MiembroEquipo;
import com.esports.teamservice.models.dtos.EquipoDTO;
import com.esports.teamservice.models.dtos.MiembroEquipoDTO;
import com.esports.teamservice.repositories.EquipoRepository;
import com.esports.teamservice.repositories.MiembroEquipoRepository;
import feign.FeignException;
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
public class EquipoServiceImplTest {

    @Mock
    private EquipoRepository equipoRepository;

    @Mock
    private MiembroEquipoRepository miembroEquipoRepository;

    @Mock
    private UsuarioClient usuarioClient;

    @Mock
    private JuegoClient juegoClient;

    @InjectMocks
    private EquipoServiceImpl equipoService;

    private Equipo equipoPrueba;
    private EquipoDTO equipoDTO;

    @BeforeEach
    public void setUp() {
        equipoPrueba = new Equipo();
        equipoPrueba.setEquipoId(1L);
        equipoPrueba.setNombre("Los Campeones");
        equipoPrueba.setCapitanId(10L);
        equipoPrueba.setJuegoPrincipalId(2L);
        equipoPrueba.setEstado(true);

        equipoDTO = new EquipoDTO();
        equipoDTO.setNombre("Los Campeones");
        equipoDTO.setCapitanId(10L);
        equipoDTO.setJuegoPrincipalId(2L);
    }

    @Test
    @DisplayName("Debe retornar todos los equipos")
    public void shouldFindAllEquipos() {
        // Given
        when(equipoRepository.findAll()).thenReturn(List.of(equipoPrueba));

        // When
        List<Equipo> result = equipoService.findAll();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNombre()).isEqualTo("Los Campeones");
        verify(equipoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe encontrar un equipo por su id")
    public void shouldFindEquipoById() {
        // Given
        when(equipoRepository.findById(1L)).thenReturn(Optional.of(equipoPrueba));

        // When
        Equipo result = equipoService.findById(1L);

        // Then
        assertThat(result.getEquipoId()).isEqualTo(1L);
        assertThat(result.getNombre()).isEqualTo("Los Campeones");
        verify(equipoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar EquipoException cuando el equipo no existe")
    public void shouldThrowWhenEquipoNotFound() {
        // Given
        when(equipoRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> equipoService.findById(999L))
                .isInstanceOf(EquipoException.class)
                .hasMessageContaining("999");

        verify(equipoRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Debe retornar equipos filtrados por estado")
    public void shouldFindEquiposByEstado() {
        // Given
        when(equipoRepository.findByEstado(true)).thenReturn(List.of(equipoPrueba));

        // When
        List<Equipo> result = equipoService.findByEstado(true);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEstado()).isTrue();
        verify(equipoRepository, times(1)).findByEstado(true);
    }

    @Test
    @DisplayName("Debe guardar un equipo cuando capitan y juego existen")
    public void shouldSaveEquipo() {
        // Given
        when(equipoRepository.findByNombre("Los Campeones")).thenReturn(Optional.empty());
        when(usuarioClient.getUsuarioById(10L)).thenReturn(null);
        when(juegoClient.getJuegoById(2L)).thenReturn(null);
        when(equipoRepository.save(any(Equipo.class))).thenReturn(equipoPrueba);

        // When
        Equipo result = equipoService.save(equipoDTO);

        // Then
        assertThat(result.getNombre()).isEqualTo("Los Campeones");
        assertThat(result.getEstado()).isTrue();
        verify(equipoRepository, times(1)).save(any(Equipo.class));
    }

    @Test
    @DisplayName("Debe lanzar excepcion si el nombre del equipo ya existe")
    public void shouldThrowWhenNombreDuplicado() {
        // Given
        when(equipoRepository.findByNombre("Los Campeones")).thenReturn(Optional.of(equipoPrueba));

        // When & Then
        assertThatThrownBy(() -> equipoService.save(equipoDTO))
                .isInstanceOf(EquipoException.class)
                .hasMessageContaining("Los Campeones");

        verify(equipoRepository, never()).save(any(Equipo.class));
    }

    @Test
    @DisplayName("Debe lanzar excepcion si el capitan no existe en user-service")
    public void shouldThrowWhenCapitanNoExiste() {
        // Given
        when(equipoRepository.findByNombre("Los Campeones")).thenReturn(Optional.empty());
        when(usuarioClient.getUsuarioById(10L)).thenThrow(mock(FeignException.class));

        // When & Then
        assertThatThrownBy(() -> equipoService.save(equipoDTO))
                .isInstanceOf(EquipoException.class)
                .hasMessageContaining("10");

        verify(equipoRepository, never()).save(any(Equipo.class));
    }

    @Test
    @DisplayName("Debe lanzar excepcion si el juego no existe en game-service")
    public void shouldThrowWhenJuegoNoExiste() {
        // Given
        when(equipoRepository.findByNombre("Los Campeones")).thenReturn(Optional.empty());
        when(usuarioClient.getUsuarioById(10L)).thenReturn(null);
        when(juegoClient.getJuegoById(2L)).thenThrow(mock(FeignException.class));

        // When & Then
        assertThatThrownBy(() -> equipoService.save(equipoDTO))
                .isInstanceOf(EquipoException.class)
                .hasMessageContaining("2");

        verify(equipoRepository, never()).save(any(Equipo.class));
    }

    @Test
    @DisplayName("Debe desactivar un equipo cambiando su estado a false")
    public void shouldDesactivarEquipo() {
        // Given
        when(equipoRepository.findById(1L)).thenReturn(Optional.of(equipoPrueba));
        when(equipoRepository.save(any(Equipo.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        Equipo result = equipoService.desactivar(1L);

        // Then
        assertThat(result.getEstado()).isFalse();
        verify(equipoRepository, times(1)).save(equipoPrueba);
    }

    @Test
    @DisplayName("Debe agregar un miembro a un equipo activo")
    public void shouldAgregarMiembro() {
        // Given
        MiembroEquipoDTO dto = new MiembroEquipoDTO();
        dto.setEquipoId(1L);
        dto.setUsuarioId(5L);
        dto.setRolDentroEquipo("SUPLENTE");

        MiembroEquipo miembro = new MiembroEquipo();
        miembro.setEquipoId(1L);
        miembro.setUsuarioId(5L);
        miembro.setRolDentroEquipo("SUPLENTE");

        when(equipoRepository.findById(1L)).thenReturn(Optional.of(equipoPrueba));
        when(usuarioClient.getUsuarioById(5L)).thenReturn(null);
        when(miembroEquipoRepository.findByEquipoIdAndUsuarioId(1L, 5L)).thenReturn(Optional.empty());
        when(miembroEquipoRepository.save(any(MiembroEquipo.class))).thenReturn(miembro);

        // When
        MiembroEquipo result = equipoService.agregarMiembro(dto);

        // Then
        assertThat(result.getUsuarioId()).isEqualTo(5L);
        verify(miembroEquipoRepository, times(1)).save(any(MiembroEquipo.class));
    }

    @Test
    @DisplayName("Debe lanzar excepcion al agregar miembro a equipo inactivo")
    public void shouldThrowWhenEquipoInactivo() {
        // Given
        equipoPrueba.setEstado(false);
        MiembroEquipoDTO dto = new MiembroEquipoDTO();
        dto.setEquipoId(1L);
        dto.setUsuarioId(5L);
        dto.setRolDentroEquipo("SUPLENTE");

        when(equipoRepository.findById(1L)).thenReturn(Optional.of(equipoPrueba));

        // When & Then
        assertThatThrownBy(() -> equipoService.agregarMiembro(dto))
                .isInstanceOf(EquipoException.class)
                .hasMessageContaining("inactivo");

        verify(miembroEquipoRepository, never()).save(any(MiembroEquipo.class));
    }

    @Test
    @DisplayName("Debe lanzar excepcion si el usuario ya es miembro del equipo")
    public void shouldThrowWhenMiembroDuplicado() {
        // Given
        MiembroEquipoDTO dto = new MiembroEquipoDTO();
        dto.setEquipoId(1L);
        dto.setUsuarioId(5L);
        dto.setRolDentroEquipo("TITULAR");

        MiembroEquipo existente = new MiembroEquipo();
        existente.setEquipoId(1L);
        existente.setUsuarioId(5L);

        when(equipoRepository.findById(1L)).thenReturn(Optional.of(equipoPrueba));
        when(usuarioClient.getUsuarioById(5L)).thenReturn(null);
        when(miembroEquipoRepository.findByEquipoIdAndUsuarioId(1L, 5L)).thenReturn(Optional.of(existente));

        // When & Then
        assertThatThrownBy(() -> equipoService.agregarMiembro(dto))
                .isInstanceOf(EquipoException.class)
                .hasMessageContaining("miembro");

        verify(miembroEquipoRepository, never()).save(any(MiembroEquipo.class));
    }
}
