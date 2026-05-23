package com.esports.sanctionservice.repositories;

import com.esports.sanctionservice.models.Sancion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SancionRepository extends JpaRepository<Sancion, Long> {

    List<Sancion> findByUsuarioId(Long usuarioId);
    List<Sancion> findByEsquipoId(Long equipoId);
    List<Sancion> findByEstado(String estado);

    //esto buscara sanciones activas de algun usuario para bloquear su inscripcion
    List<Sancion> findByUsuarioIdAndEstado(Long usuarioId, String estado);
    List<Sancion> findByEquipoIdAndEstado(Long esquipoId, String estado);

}
