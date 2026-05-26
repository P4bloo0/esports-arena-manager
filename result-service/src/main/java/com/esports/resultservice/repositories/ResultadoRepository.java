package com.esports.resultservice.repositories;

import com.esports.resultservice.models.Resultado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;


@Repository
public interface ResultadoRepository extends JpaRepository<Resultado, Long> {


    Optional<Resultado> findByPartidaId(Long partidaId);


    List<Resultado> findByPartidaIdIn(List<Long> partidaIds);
}
