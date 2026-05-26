package com.esports.notificationservice.repositories;

import com.esports.notificationservice.models.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {


    List<Notificacion> findByUsuarioId(Long usuarioId);


    List<Notificacion> findByEquipoId(Long equipoId);


    List<Notificacion> findByUsuarioIdAndLeida(Long usuarioId, Boolean leida);

    List<Notificacion> findByUsuarioIdAndEstado(Long usuarioId, Notificacion.Estado estado);
}
