package com.esports.registrationservice.services;
import com.esports.registrationservice.repositories.CupoRepository;
import com.esports.registrationservice.models.CupoTorneo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;
@Service
public class CupoService {
 private final CupoRepository repo; public CupoService(CupoRepository repo) { this.repo=repo; }
 @Transactional(propagation=Propagation.REQUIRES_NEW) public void ensure(Long id) { if(!repo.existsById(id)) repo.saveAndFlush(new CupoTorneo(id)); }
 public void lock(Long id) { repo.lock(id).orElseThrow(); }
}
