# eSports Arena Manager

Plataforma backend distribuida para gestionar torneos de videojuegos competitivos. Permite administrar juegos, jugadores, equipos, torneos, inscripciones, partidas, resultados, rankings, sanciones y notificaciones.

## Integrantes
- Pablo Salas
- Benjamín Villalón

## Tecnologías
- Java 21
- Spring Boot 4.0.6
- Spring Data JPA + Hibernate
- MySQL
- Spring Cloud OpenFeign
- Netflix Eureka
- Spring Cloud Gateway
- Spring Security + OAuth2 Resource Server (JWT)
- Bean Validation
- SLF4J
- Lombok
- JUnit 5 + Mockito
- Docker
- Maven

## Microservicios

| Servicio | Puerto | Descripción |
|---|---|---|
| eureka-service | 8761 | Registro y descubrimiento de servicios |
| gateway-service | 8080 | Punto de entrada único, enrutamiento centralizado |
| auth-service | 8011 | Registro, login y emisión de JWT |
| user-service | 8001 | Gestiona jugadores, organizadores y administradores |
| game-service | 8002 | Administra los videojuegos disponibles para torneos |
| team-service | 8003 | Gestiona equipos y sus integrantes |
| tournament-service | 8004 | Administra torneos, fechas y cupos |
| registration-service | 8005 | Maneja inscripciones de equipos a torneos |
| match-service | 8006 | Gestiona las partidas dentro de cada torneo |
| result-service | 8007 | Registra y valida resultados de partidas |
| ranking-service | 8008 | Calcula y mantiene la tabla de posiciones |
| sanction-service | 8009 | Administra sanciones a jugadores y equipos |
| notification-service | 8010 | Gestiona notificaciones internas del sistema |

## Como ejecutar (Laragon)

1. Clonar el repositorio
```bash
git clone https://github.com/P4bloo0/esports-arena-manager.git
```
2. Abrir Laragon y darle Start All (o al menos que MySQL quede corriendo en el puerto 3306, usuario root, sin contraseña)
3. Abrir el proyecto en IntelliJ IDEA
4. `mvn clean install` desde la raíz para bajar las dependencias
5. Ejecutar cada Application.java en este orden:
    - eureka-service (8761)
    - gateway-service (8080)
    - auth-service (8011)
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
6. Verificar en http://localhost:8761 que todos los servicios estén UP
7. Las bases de datos se crean solas la primera vez que cada servicio se conecta (createDatabaseIfNotExist=true), no hace falta crearlas a mano en Laragon
8. Probar con Postman a través del Gateway en http://localhost:8080/api/v1/...

## Autenticación

Todos los endpoints de negocio requieren un JWT válido, salvo Swagger/API docs. El token se obtiene desde auth-service:

```
POST http://localhost:8080/api/v1/auth/register
{
    "nickname": "usuario",
    "password": "clave123"
}
```

```
POST http://localhost:8080/api/v1/auth/login
{
    "nickname": "usuario",
    "password": "clave123"
}
```

Ambos devuelven un token que se debe enviar en cada petición posterior:

```
Authorization: Bearer <token>
```

## Como ejecutar con Docker

```bash
docker compose up --build
```

Levanta un MySQL propio en contenedor (puerto 3307 en el host, para no chocar con Laragon) junto con los 13 microservicios. Eureka queda en localhost:8761 y el Gateway en localhost:8080.

## Flujo principal

Antes de cualquier prueba, registrarse o loguearse en auth-service y usar el token en el header Authorization de cada petición.

Para probar el sistema completo sigue este orden:
1. Crear un juego en game-service
2. Crear usuarios en user-service
3. Crear un equipo en team-service con el usuario como capitán
4. Crear un torneo en tournament-service con el juego
5. Cambiar estado del torneo a ABIERTO
6. Inscribir el equipo en registration-service
7. Crear partidas en match-service
8. Registrar resultados en result-service
9. Ver el ranking en ranking-service

## Endpoints

### user-service (8001)
Gestiona los usuarios del sistema. Cada usuario tiene un rol y un estado. Un usuario inactivo no puede competir.

Roles disponibles: JUGADOR, ORGANIZADOR, ADMINISTRADOR
Estados disponibles: ACTIVO, INACTIVO, SANCIONADO

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
Administra los videojuegos disponibles para torneos. Un juego inactivo no puede usarse en nuevos torneos.

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
Gestiona los equipos y sus integrantes. El capitán debe existir en user-service y el juego en game-service antes de crear un equipo.

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
Gestiona los torneos. El juego debe existir antes de crear un torneo. La fecha de inicio debe ser posterior al cierre de inscripciones.

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
Gestiona las sanciones a jugadores y equipos. Un participante sancionado no puede inscribirse a torneos.

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
Gestiona las inscripciones a torneos. El torneo debe estar en estado ABIERTO y el participante no debe tener sanciones activas.

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
Gestiona las partidas dentro de los torneos. Los participantes deben estar inscritos en el torneo.

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
Registra y valida los resultados de las partidas. La partida debe existir y no estar cancelada.

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
Calcula y mantiene la tabla de posiciones de cada torneo basandose en los resultados validados.

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
Gestiona las notificaciones internas del sistema para mantener informados a jugadores y organizadores.

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