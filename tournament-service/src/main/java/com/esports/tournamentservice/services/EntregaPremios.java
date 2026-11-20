package com.esports.tournamentservice.services;
import com.esports.tournamentservice.repositories.EntregaRepository;
import com.esports.tournamentservice.clients.PremioNotificationClient;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.*;
import org.springframework.transaction.support.*;
import org.springframework.transaction.*;
@Service @EnableScheduling
public class EntregaPremios {
 private final EntregaRepository repo; private final PremioNotificationClient client; private final TransactionTemplate tx;
 private static final org.slf4j.Logger log=org.slf4j.LoggerFactory.getLogger(EntregaPremios.class);
 public EntregaPremios(EntregaRepository repo,PremioNotificationClient client,PlatformTransactionManager manager) { this.repo=repo; this.client=client; tx=new TransactionTemplate(manager); tx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW); }
 public void process(String id) { try { tx.executeWithoutResult(status -> repo.findById(id).ifPresent(job -> { client.enviar(new PremioNotificationClient.Notificacion(job.getUsuarioId(),"PREMIO",job.getMensaje(),job.getId())); repo.delete(job); })); } catch(Exception e) { log.warn("Notificacion de premio pendiente: {}",id); } }
 @Scheduled(fixedDelayString="${arena.retry-ms:5000}") public void retry() { repo.findAll().forEach(job -> process(job.getId())); }
}
