package com.esports.tournamentservice.models;
import jakarta.persistence.*;
import lombok.*;
@Entity @Getter @Setter @NoArgsConstructor
public class EntregaPendiente { @Id private String id; private Long usuarioId; private String mensaje; }
