package com.esports.rankingservice.services;
import com.esports.rankingservice.models.RankingControl;
import com.esports.rankingservice.repositories.RankingControlRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;
@Service public class RankingControlService {
 private final RankingControlRepository repo; public RankingControlService(RankingControlRepository repo) { this.repo=repo; }
 @Transactional(propagation=Propagation.REQUIRES_NEW) public void ensure(Long id) { if(!repo.existsById(id)) repo.saveAndFlush(new RankingControl(id)); }
 public void lock(Long id) { repo.lock(id).orElseThrow(); }
}
