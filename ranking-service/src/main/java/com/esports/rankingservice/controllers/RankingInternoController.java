package com.esports.rankingservice.controllers;
import org.springframework.web.bind.annotation.*;
import com.esports.rankingservice.services.RankingService;
import com.esports.rankingservice.models.Ranking;
import java.util.List;
@RestController
public class RankingInternoController {
 private final RankingService service; public RankingInternoController(RankingService service) { this.service=service; }
 @PostMapping("/internal/rankings/{id}/recalcular") public List<Ranking> recalculate(@PathVariable Long id) { return service.recalcularDesdeResultados(id); }
}
