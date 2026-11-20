package com.esports.tournamentservice.repositories;
import com.esports.tournamentservice.models.Premio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface PremioRepository extends JpaRepository<Premio,Long> { List<Premio> findByTorneoIdOrderByPosicionAsc(Long torneoId); Optional<Premio> findByTorneoIdAndPosicion(Long torneoId,Integer posicion); }
