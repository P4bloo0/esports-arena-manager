package com.esports.notificationservice.services;

import com.esports.notificationservice.exceptions.NotificacionException;
import com.esports.notificationservice.models.Notificacion;
import com.esports.notificationservice.models.dtos.NotificacionDTO;
import com.esports.notificationservice.repositories.NotificacionRepository;
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
public class NotificacionServiceImplTest {

    @Mock
    private NotificacionRepository notificacionRepository;

    @InjectMocks
    private NotificacionServiceImpl notificacionService;

    private Notificacion notificacionPrueba;
    private NotificacionDTO notificacionDTO;

    @BeforeEach
    public void setUp() {
        notificacionPrueba = new Notificacion();
        notificacionPrueba.setNotificacionId(1L);
        notificacionPrueba.setUsuarioId(10L);
        notificacionPrueba.setTipo(Notificacion.TipoNotificacion.RESULTADO);
        notificacionPrueba.setMensaje("Tu partida ha finalizado");
        notificacionPrueba.setLeida(false);
        notificacionPrueba.setEstado(Notificacion.Estado.ACTIVA);

        notificacionDTO = new NotificacionDTO();
        notificacionDTO.setUsuarioId(10L);
        notificacionDTO.setTipo(Notificacion.TipoNotificacion.RESULTADO);
        notificacionDTO.setMensaje("Tu partida ha finalizado");
    }

    @Test
    @DisplayName("Debe retornar todas las notificaciones")
    public void shouldFindAllNotificaciones() {
        when(notificacionRepository.findAll()).thenReturn(List.of(notificacionPrueba));

        List<Notificacion> result = notificacionService.findAll();

        assertThat(result).hasSize(1);
        verify(notificacionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe encontrar una notificacion por su id")
    public void shouldFindNotificacionById() {
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacionPrueba));

        Notificacion result = notificacionService.findById(1L);

        assertThat(result.getMensaje()).isEqualTo("Tu partida ha finalizado");
        verify(notificacionRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando la notificacion no existe")
    public void shouldThrowWhenNotificacionNotFound() {
        when(notificacionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificacionService.findById(999L))
                .isInstanceOf(NotificacionException.class);
        verify(notificacionRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Debe crear una notificacion con destinatario valido")
    public void shouldSaveNotificacion() {
        when(notificacionRepository.save(any(Notificacion.class))).thenAnswer(inv -> inv.getArgument(0));

        Notificacion result = notificacionService.save(notificacionDTO);

        assertThat(result).isNotNull();
        assertThat(result.getLeida()).isFalse();
        assertThat(result.getEstado()).isEqualTo(Notificacion.Estado.ACTIVA);
        verify(notificacionRepository, times(1)).save(any(Notificacion.class));
    }

    @Test
    @DisplayName("Debe lanzar excepcion al crear una notificacion sin destinatario")
    public void shouldNotSaveWhenSinDestinatario() {
        notificacionDTO.setUsuarioId(null);
        notificacionDTO.setEquipoId(null);

        assertThatThrownBy(() -> notificacionService.save(notificacionDTO))
                .isInstanceOf(NotificacionException.class);
        verify(notificacionRepository, never()).save(any(Notificacion.class));
    }

    @Test
    @DisplayName("Debe marcar una notificacion como leida")
    public void shouldMarcarComoLeida() {
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacionPrueba));
        when(notificacionRepository.save(any(Notificacion.class))).thenAnswer(inv -> inv.getArgument(0));

        Notificacion result = notificacionService.marcarComoLeida(1L);

        assertThat(result.getLeida()).isTrue();
        verify(notificacionRepository, times(1)).save(notificacionPrueba);
    }

    @Test
    @DisplayName("Debe lanzar excepcion al marcar como leida una notificacion ya leida")
    public void shouldNotMarcarComoLeidaWhenYaLeida() {
        notificacionPrueba.setLeida(true);
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacionPrueba));

        assertThatThrownBy(() -> notificacionService.marcarComoLeida(1L))
                .isInstanceOf(NotificacionException.class);
        verify(notificacionRepository, never()).save(any(Notificacion.class));
    }

    @Test
    @DisplayName("Debe archivar una notificacion activa")
    public void shouldArchivarNotificacion() {
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacionPrueba));
        when(notificacionRepository.save(any(Notificacion.class))).thenAnswer(inv -> inv.getArgument(0));

        Notificacion result = notificacionService.archivar(1L);

        assertThat(result.getEstado()).isEqualTo(Notificacion.Estado.ARCHIVADA);
        verify(notificacionRepository, times(1)).save(notificacionPrueba);
    }

    @Test
    @DisplayName("Debe lanzar excepcion al archivar una notificacion ya archivada")
    public void shouldNotArchivarWhenYaArchivada() {
        notificacionPrueba.setEstado(Notificacion.Estado.ARCHIVADA);
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacionPrueba));

        assertThatThrownBy(() -> notificacionService.archivar(1L))
                .isInstanceOf(NotificacionException.class);
        verify(notificacionRepository, never()).save(any(Notificacion.class));
    }

    @Test
    @DisplayName("Debe listar las notificaciones no leidas de un usuario")
    public void shouldFindNoLeidasByUsuarioId() {
        when(notificacionRepository.findByUsuarioIdAndLeida(10L, false)).thenReturn(List.of(notificacionPrueba));

        List<Notificacion> result = notificacionService.findNoLeidasByUsuarioId(10L);

        assertThat(result).hasSize(1);
        verify(notificacionRepository, times(1)).findByUsuarioIdAndLeida(10L, false);
    }

    @Test
    @DisplayName("Debe listar las notificaciones de un equipo")
    public void shouldFindByEquipoId() {
        when(notificacionRepository.findByEquipoId(50L)).thenReturn(List.of(notificacionPrueba));

        List<Notificacion> result = notificacionService.findByEquipoId(50L);

        assertThat(result).hasSize(1);
        verify(notificacionRepository, times(1)).findByEquipoId(50L);
    }
}
