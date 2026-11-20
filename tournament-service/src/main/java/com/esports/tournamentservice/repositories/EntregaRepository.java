package com.esports.tournamentservice.repositories;
import com.esports.tournamentservice.models.EntregaPendiente;
import org.springframework.data.jpa.repository.JpaRepository;
public interface EntregaRepository extends JpaRepository<EntregaPendiente,String> {}
