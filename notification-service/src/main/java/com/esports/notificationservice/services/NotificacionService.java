package com.esports.notificationservice.services;

import com.esports.notificationservice.models.Notificacion;
import com.esports.notificationservice.models.dtos.NotificacionDTO;
import java.util.List;


public interface NotificacionService {

    List<Notificacion> findAll();
    Notificacion findById(Long id);
    List<Notificacion> findByUsuarioId(Long usuarioId);
    List<Notificacion> findByEquipoId(Long equipoId);
    List<Notificacion> findNoLeidasByUsuarioId(Long usuarioId);
    Notificacion save(NotificacionDTO dto);
    Notificacion marcarComoLeida(Long id);
    Notificacion archivar(Long id);
}
