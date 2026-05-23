package com.esports.teamservice.repositories;


import com.esports.teamservice.models.Equipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface EquipoRepository extends JpaRepository<Equipo, Long> {

    //busca equipos por nombre para validar de que no se repitan
    Optional<Equipo> findByNombre(String nombre);

    //lista los equipos por estado
    List<Equipo> findByEstado(Boolean estado);
}
