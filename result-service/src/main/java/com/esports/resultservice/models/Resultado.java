package com.esports.resultservice.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "resultados")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Resultado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resultado_id")
    private Long resultadoId;


    @NotNull(message = "La partida es obligatoria")
    @Column(name = "partida_id", nullable = false, unique = true)
    private Long partidaId;


    @NotNull(message = "El ganador es obligatorio")
    @Column(name = "ganador_id", nullable = false)
    private Long ganadorId;


    @Min(value = 0, message = "El puntaje A no puede ser negativo")
    @Column(name = "puntaje_a", nullable = false)
    private Integer puntajeA;


    @Min(value = 0, message = "El puntaje B no puede ser negativo")
    @Column(name = "puntaje_b", nullable = false)
    private Integer puntajeB;


    @Enumerated(EnumType.STRING)
    @Column(name = "estado_validacion", nullable = false)
    private EstadoValidacion estadoValidacion = EstadoValidacion.PENDIENTE;


    @Column(name = "justificacion_anulacion")
    private String justificacionAnulacion;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();


    public enum EstadoValidacion {
        PENDIENTE,
        VALIDADO,
        ANULADO
    }
}
