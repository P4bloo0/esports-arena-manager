package com.esports.rankingservice.repositories;
import org.springframework.data.jpa.repository.*;
import jakarta.persistence.LockModeType;
import com.esports.rankingservice.models.RankingControl;
public interface RankingControlRepository extends JpaRepository<RankingControl,Long> { @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select r from RankingControl r where r.torneoId=:id") java.util.Optional<RankingControl> lock(Long id); }
