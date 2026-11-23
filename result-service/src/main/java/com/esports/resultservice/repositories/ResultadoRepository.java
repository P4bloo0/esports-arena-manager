package com.esports.resultservice.repositories;

import com.esports.resultservice.models.Resultado;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;


@Repository
public interface ResultadoRepository extends JpaRepository<Resultado, Long> {


    Optional<Resultado> findByPartidaId(Long partidaId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Resultado r where r.resultadoId = :id")
    Optional<Resultado> lock(Long id);

    List<Resultado> findByPartidaIdIn(List<Long> partidaIds);
}
