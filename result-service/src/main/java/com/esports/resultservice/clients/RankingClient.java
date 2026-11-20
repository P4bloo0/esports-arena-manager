package com.esports.resultservice.clients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
@FeignClient(name="ranking-service",contextId="resultRanking",path="/internal/rankings")
public interface RankingClient { @PostMapping("/{id}/recalcular") void recalcular(@PathVariable Long id); }
