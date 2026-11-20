package com.esports.registrationservice.models;
import jakarta.persistence.*;
@Entity @Table(name="cupos_torneo")
public class CupoTorneo {
 @Id private Long torneoId;
 public CupoTorneo() {} public CupoTorneo(Long id) { torneoId=id; }
}
