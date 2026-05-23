package com.esports.sanctionservice.repositories;

import com.esports.sanctionservice.models.Sancion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SancionRepository extends JpaRepository<Sancion, Long> {

    //busca por ID sanciones
    List<Sancion> findByUsuarioId(Long usuarioId);

    //busca sanciones por equipo
    List<Sancion> findByEsquipoId(Long equipoId);


    //buscará sanciones activas por usuario o equipo
    List<Sancion> findByUsuarioIdAndEstado(Long usuarioId, String estado);
    List<Sancion> findByEquipoIdAndEstado(Long equipoId, String estado);

}
