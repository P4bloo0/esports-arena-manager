package com.esports.registrationservice.repositories;
import com.esports.registrationservice.models.CupoTorneo;
import org.springframework.data.jpa.repository.*;
import jakarta.persistence.LockModeType;
import java.util.Optional;
public interface CupoRepository extends JpaRepository<CupoTorneo,Long> {
 @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select c from CupoTorneo c where c.torneoId=:id") Optional<CupoTorneo> lock(Long id);
}
