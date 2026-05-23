package com.esports.teamservice.repositories;

import com.esports.teamservice.models.MiembroEquipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MiembroEquipoRepository extends JpaRepository<MiembroEquipo, Long> {

    List<MiembroEquipo> findByEquipoId(Long equipoId);

    Optional<MiembroEquipo> findByEquipoIdAndUsuarioId(Long equipoId, Long usuarioId);
}
