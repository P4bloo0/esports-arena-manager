package com.esports.resultservice.services;
import com.esports.resultservice.models.RankingPendiente;
import com.esports.resultservice.repositories.RankingPendienteRepository;
import com.esports.resultservice.clients.*;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.*;
import org.springframework.transaction.support.*;
import org.springframework.transaction.*;
@Service @EnableScheduling
public class RankingSync {
 private final RankingPendienteRepository repo; private final RankingClient rankings; private final PartidaClient partidas; private final TransactionTemplate tx;
 private static final org.slf4j.Logger log=org.slf4j.LoggerFactory.getLogger(RankingSync.class);
 public RankingSync(RankingPendienteRepository repo,RankingClient rankings,PartidaClient partidas,PlatformTransactionManager manager) { this.repo=repo; this.rankings=rankings; this.partidas=partidas; tx=new TransactionTemplate(manager); tx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW); }
 public void enqueue(Long partida) {
  var job=repo.save(new RankingPendiente(partida));
  TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() { @Override public void afterCommit() { process(job.getId()); } });
 }
 public void process(String id) {
  try { tx.executeWithoutResult(status -> repo.findById(id).ifPresent(job -> { rankings.recalcular(partidas.getPartidaById(job.getPartidaId()).getTorneoId()); repo.delete(job); })); }
  catch(Exception e) { log.warn("Ranking pendiente: se reintentara la entrega {}",id); }
 }
 @Scheduled(fixedDelayString="${arena.retry-ms:5000}") public void retry() { repo.findAll().forEach(job -> process(job.getId())); }
}
