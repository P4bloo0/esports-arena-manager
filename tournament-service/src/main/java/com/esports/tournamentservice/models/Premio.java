package com.esports.tournamentservice.models;
import jakarta.persistence.*;
import lombok.*;
@Entity @Getter @Setter @NoArgsConstructor
@Table(name="premios",uniqueConstraints=@UniqueConstraint(columnNames={"torneo_id","posicion"}))
public class Premio {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long premioId;
 @Column(name="torneo_id",nullable=false) private Long torneoId;
 @Column(nullable=false) private Integer posicion;
 @Column(nullable=false) private String descripcion;
 private Long participanteId;
 private String tipoParticipante;
}
