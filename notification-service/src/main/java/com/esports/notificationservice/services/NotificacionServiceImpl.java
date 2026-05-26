package com.esports.notificationservice.services;

import com.esports.notificationservice.exceptions.NotificacionException;
import com.esports.notificationservice.models.Notificacion;
import com.esports.notificationservice.models.dtos.NotificacionDTO;
import com.esports.notificationservice.repositories.NotificacionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificacionServiceImpl implements NotificacionService {

    private static final Logger log = LoggerFactory.getLogger(NotificacionServiceImpl.class);

    @Autowired
    private NotificacionRepository notificacionRepository;


    @Transactional(readOnly = true)
    @Override
    public List<Notificacion> findAll() {
        log.info("Consultando todas las notificaciones");
        return this.notificacionRepository.findAll();
    }


    @Transactional(readOnly = true)
    @Override
    public Notificacion findById(Long id) {
        log.info("Buscando notificacion con id {}", id);
        return this.notificacionRepository.findById(id)
                .orElseThrow(() -> new NotificacionException("Notificacion con id '" + id + "' no encontrada"));
    }


    @Transactional(readOnly = true)
    @Override
    public List<Notificacion> findByUsuarioId(Long usuarioId) {
        log.info("Consultando notificaciones del usuario con id {}", usuarioId);
        return this.notificacionRepository.findByUsuarioId(usuarioId);
    }


    @Transactional(readOnly = true)
    @Override
    public List<Notificacion> findByEquipoId(Long equipoId) {
        log.info("Consultando notificaciones del equipo con id {}", equipoId);
        return this.notificacionRepository.findByEquipoId(equipoId);
    }


    @Transactional(readOnly = true)
    @Override
    public List<Notificacion> findNoLeidasByUsuarioId(Long usuarioId) {
        log.info("Consultando notificaciones no leidas del usuario con id {}", usuarioId);
        return this.notificacionRepository.findByUsuarioIdAndLeida(usuarioId, false);
    }


    @Transactional
    @Override
    public Notificacion save(NotificacionDTO dto) {
        log.info("Creando notificacion de tipo {} ", dto.getTipo());


        if (dto.getUsuarioId() == null && dto.getEquipoId() == null) {
            log.warn("Intento de crear notificacion sin destinatario");
            throw new NotificacionException("La notificacion debe tener al menos un destinatario: usuarioId o equipoId");
        }

        Notificacion notificacion = new Notificacion();
        notificacion.setUsuarioId(dto.getUsuarioId());
        notificacion.setEquipoId(dto.getEquipoId());
        notificacion.setTipo(dto.getTipo());
        notificacion.setMensaje(dto.getMensaje());
        notificacion.setLeida(false);
        notificacion.setFecha(LocalDateTime.now());
        notificacion.setEstado(Notificacion.Estado.ACTIVA);

        Notificacion guardada = this.notificacionRepository.save(notificacion);
        log.info("Notificacion creada exitosamente con id {}", guardada.getNotificacionId());
        return guardada;
    }


    @Transactional
    @Override
    public Notificacion marcarComoLeida(Long id) {
        log.info("Marcando notificacion con id {} como leida", id);
        return this.notificacionRepository.findById(id).map(notificacion -> {

            if (notificacion.getLeida()) {
                throw new NotificacionException("La notificacion ya fue marcada como leida");
            }

            notificacion.setLeida(true);
            return this.notificacionRepository.save(notificacion);
        }).orElseThrow(() -> new NotificacionException("Notificacion con id '" + id + "' no encontrada"));
    }


    @Transactional
    @Override
    public Notificacion archivar(Long id) {
        log.info("Archivando notificacion con id {}", id);
        return this.notificacionRepository.findById(id).map(notificacion -> {

            if (notificacion.getEstado() == Notificacion.Estado.ARCHIVADA) {
                throw new NotificacionException("La notificacion ya esta archivada");
            }

            notificacion.setEstado(Notificacion.Estado.ARCHIVADA);
            return this.notificacionRepository.save(notificacion);
        }).orElseThrow(() -> new NotificacionException("Notificacion con id '" + id + "' no encontrada"));
    }
}
