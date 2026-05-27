# eSports Arena Manager

Plataforma backend distribuida para gestionar torneos de videojuegos competitivos.

## Integrantes
- Pablo Salas
- Benjamín Villalón

## Tecnologías
- Java 21
- Spring Boot 4.0.6
- Spring Data JPA + Hibernate
- H2 Database
- Spring Cloud OpenFeign
- Bean Validation
- SLF4J
- Lombok
- Maven

## Microservicios

| Servicio | Puerto |
|---|---|
| user-service | 8001 |
| game-service | 8002 |
| team-service | 8003 |
| tournament-service | 8004 |
| registration-service | 8005 |
| match-service | 8006 |
| result-service | 8007 |
| ranking-service | 8008 |
| sanction-service | 8009 |
| notification-service | 8010 |

## Como ejecutar

1. Clonar el repositorio
```bash
git clone https://github.com/P4bloo0/esports-arena-manager.git
```
2. Abrir en IntelliJ IDEA
3. Esperar que Maven descargue las dependencias
4. Ejecutar cada Application.java en este orden
    - user-service (8001)
    - game-service (8002)
    - team-service (8003)
    - tournament-service (8004)
    - sanction-service (8009)
    - registration-service (8005)
    - match-service (8006)
    - result-service (8007)
    - ranking-service (8008)
    - notification-service (8010)
5. Probar con Postman en http://localhost:800X/api/v1/

## Endpoints

### user-service (8001)
Roles disponibles: JUGADOR, ORGANIZADOR, ADMINISTRADOR

- GET /api/v1/usuarios
- GET /api/v1/usuarios/1
- GET /api/v1/usuarios/rol/JUGADOR
- GET /api/v1/usuarios/estado/ACTIVO
- POST /api/v1/usuarios
```json
{
    "nombre": "Juan Perez",
    "nickname": "JuanGamer99",
    "email": "juan@esports.com",
    "rol": "JUGADOR"
}
```
- PUT /api/v1/usuarios/1
- DELETE /api/v1/usuarios/1

### game-service (8002)

- GET /api/v1/juegos
- GET /api/v1/juegos/activos
- GET /api/v1/juegos/1
- POST /api/v1/juegos
```json
{
    "nombre": "Valorant",
    "genero": "FPS",
    "modalidad": "5v5",
    "jugadoresPorEquipo": 5
}
```
- PUT /api/v1/juegos/1
- DELETE /api/v1/juegos/1

### team-service (8003)
El capitan y el juego deben existir antes de crear un equipo.

- GET /api/v1/equipos
- GET /api/v1/equipos/1
- GET /api/v1/equipos/estado/true
- GET /api/v1/equipos/1/miembros
- POST /api/v1/equipos
```json
{
    "nombre": "Los Invictos",
    "capitanId": 1,
    "juegoPrincipalId": 1
}
```
- POST /api/v1/equipos/miembros
```json
{
    "equipoId": 1,
    "usuarioId": 1,
    "rolDentroEquipo": "CAPITAN"
}
```
- PUT /api/v1/equipos/1
- DELETE /api/v1/equipos/1

### tournament-service (8004)
Estados disponibles: BORRADOR, ABIERTO, EN_CURSO, FINALIZADO

- GET /api/v1/torneos
- GET /api/v1/torneos/1
- GET /api/v1/torneos/estado/ABIERTO
- GET /api/v1/torneos/juego/1
- POST /api/v1/torneos
```json
{
    "nombre": "Copa Valorant 2026",
    "juegoId": 1,
    "fechaInicio": "2026-12-01T10:00:00",
    "fechaFin": "2026-12-15T18:00:00",
    "fechaCierreInscripcion": "2026-11-25T23:59:59",
    "cupoMaximo": 16,
    "modalidad": "ELIMINACION_DIRECTA"
}
```
- PUT /api/v1/torneos/1
- PUT /api/v1/torneos/1/estado?nuevo=ABIERTO
- DELETE /api/v1/torneos/1

### sanction-service (8009)
Severidades disponibles: BAJA, MEDIA, ALTA

- GET /api/v1/sanciones
- GET /api/v1/sanciones/1
- GET /api/v1/sanciones/usuario/1
- GET /api/v1/sanciones/equipo/1
- GET /api/v1/sanciones/verificar?usuarioId=1
- POST /api/v1/sanciones
```json
{
    "usuarioId": 1,
    "motivo": "Uso de hacks durante la partida",
    "fechaInicio": "2026-05-26T00:00:00",
    "fechaFin": "2026-06-26T00:00:00",
    "severidad": "ALTA"
}
```
- PUT /api/v1/sanciones/1
- DELETE /api/v1/sanciones/1

### registration-service (8005)
El torneo debe estar ABIERTO para inscribirse.

- GET /api/v1/inscripciones
- GET /api/v1/inscripciones/1
- GET /api/v1/inscripciones/torneo/1
- GET /api/v1/inscripciones/equipo/1
- GET /api/v1/inscripciones/jugador/1
- POST /api/v1/inscripciones
```json
{
    "torneoId": 1,
    "equipoId": 1,
    "tipoParticipante": "EQUIPO"
}
```
- PUT /api/v1/inscripciones/1/estado?nuevoEstado=CONFIRMADA
- DELETE /api/v1/inscripciones/1

### match-service (8006)

- GET /api/v1/partidas
- GET /api/v1/partidas/1
- GET /api/v1/partidas/torneo/1
- GET /api/v1/partidas/torneo/1/ronda/SEMIFINAL
- GET /api/v1/partidas/estado/PROGRAMADA
- POST /api/v1/partidas
```json
{
    "torneoId": 1,
    "participanteAId": 1,
    "participanteBId": 2,
    "ronda": "SEMIFINAL",
    "fechaHora": "2026-12-05T10:00:00"
}
```
- PUT /api/v1/partidas/1
- DELETE /api/v1/partidas/1

### result-service (8007)

- GET /api/v1/resultados
- GET /api/v1/resultados/1
- GET /api/v1/resultados/partida/1
- GET /api/v1/resultados/torneo/1
- POST /api/v1/resultados
```json
{
    "partidaId": 1,
    "ganadorId": 1,
    "puntajeA": 13,
    "puntajeB": 7
}
```
- PUT /api/v1/resultados/1
- DELETE /api/v1/resultados/1

### ranking-service (8008)

- GET /api/v1/rankings
- GET /api/v1/rankings/1
- GET /api/v1/rankings/torneo/1
- GET /api/v1/rankings/torneo/1/participante/1
- POST /api/v1/rankings
```json
{
    "torneoId": 1,
    "participanteId": 1,
    "puntos": 0,
    "victorias": 0,
    "derrotas": 0
}
```
- PUT /api/v1/rankings/1/puntos
- POST /api/v1/rankings/torneo/1/recalcular
- PATCH /api/v1/rankings/torneo/1/cerrar

### notification-service (8010)

- GET /api/v1/notificaciones
- GET /api/v1/notificaciones/1
- GET /api/v1/notificaciones/usuario/1
- GET /api/v1/notificaciones/equipo/1
- GET /api/v1/notificaciones/usuario/1/no-leidas
- POST /api/v1/notificaciones
```json
{
    "usuarioId": 1,
    "tipo": "INSCRIPCION",
    "mensaje": "Tu equipo ha sido inscrito correctamente al torneo"
}
```
- PATCH /api/v1/notificaciones/1/leer
- PATCH /api/v1/notificaciones/1/archivar