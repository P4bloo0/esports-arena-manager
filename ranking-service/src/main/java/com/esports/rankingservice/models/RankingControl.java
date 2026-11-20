package com.esports.rankingservice.models;
import jakarta.persistence.*;
@Entity public class RankingControl { @Id private Long torneoId; public RankingControl() {} public RankingControl(Long id) { torneoId=id; } }
