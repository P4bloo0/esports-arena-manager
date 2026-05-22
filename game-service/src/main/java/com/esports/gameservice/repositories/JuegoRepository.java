package com.esports.gameservice.repositories;

import com.esports.gameservice.models.Juego;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

// repository= habla con la base de datos de manera directa
// jpaRepository permite usar findAll, findById y save() y el deletebyid
@Repository
public interface JuegoRepository extends JpaRepository<Juego, Long> {

    // busca juegos por nombre para poder validar que no se repitan
    Optional<Juego> findByNombre(String nombre);

    // solo lista juegos que esten activos
    List<Juego> findByEstado(Boolean estado);

}
