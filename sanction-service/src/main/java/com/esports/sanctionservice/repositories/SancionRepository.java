package com.esports.sanctionservice.repositories;

import com.esports.sanctionservice.models.Sancion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SancionRepository extends JpaRepository<Sancion, Long> {

    // id sanciones
    List<Sancion> findByUsuarioId(Long usuarioId);

    // equipo sanciones
    List<Sancion> findByEquipoId(Long equipoId);


    // sanciones activas
    List<Sancion> findByUsuarioIdAndEstado(Long usuarioId, String estado);
    List<Sancion> findByEquipoIdAndEstado(Long equipoId, String estado);

}
