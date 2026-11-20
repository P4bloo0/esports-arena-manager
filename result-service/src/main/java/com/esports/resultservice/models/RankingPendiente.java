package com.esports.resultservice.models;
import jakarta.persistence.*;
@Entity public class RankingPendiente {
 @Id private String id=java.util.UUID.randomUUID().toString(); private Long partidaId;
 public RankingPendiente() {} public RankingPendiente(Long partidaId) { this.partidaId=partidaId; }
 public String getId() { return id; } public Long getPartidaId() { return partidaId; }
}
