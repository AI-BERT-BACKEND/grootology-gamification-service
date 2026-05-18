# Endpoint Test Plan

Este documento lista pruebas manuales para todos los endpoints del servicio.
El archivo complementario `docs/testing/endpoints.http` contiene las requests listas para ejecutar.

## 1) Precondiciones

1. Levantar el servicio en local:
   - `./mvnw spring-boot:run`
2. Base URL:
   - local: `http://localhost:8080`
   - docker-compose: `http://localhost:8084`
3. Estado actual de seguridad:
   - la configuracion actual permite requests sin token.

## 2) Endpoints cubiertos

1. `POST /api/v1/gamification/{userId}/points/events`
2. `GET /api/v1/gamification/{userId}/points`
3. `POST /api/v1/gamification/{userId}/achievements/unlock`
4. `GET /api/v1/gamification/{userId}/achievements`
5. `POST /api/v1/gamification/{userId}/subjects/progress`
6. `GET /api/v1/gamification/{userId}/subjects/progress`
7. `GET /api/v1/gamification/{userId}/subjects/{subjectId}/progress`

## 3) Casos por endpoint

### A. Points

1. `POST /points/events` - evento valido
   - Esperado: `200`, `pointsUpdated=true`, `xpEarned>0`.
2. `POST /points/events` - payload vacio
   - Esperado: `400`, `code=GAM-400`.
3. `POST /points/events` - evento duplicado (mismo `activityId` en historial)
   - Esperado: `200`, `pointsUpdated=false`, mensaje con `FA-04`.
4. `GET /points` - usuario con perfil
   - Esperado: `200`, resumen de puntos y racha.
5. `GET /points` - usuario inexistente
   - Esperado: `404`, `code=GAM-404`.

### B. Achievements

1. `POST /achievements/unlock` - unlock valido (`PERFECT_SCORE` + `score=100`)
   - Esperado: `200`, `achievementUnlocked=true`.
2. `POST /achievements/unlock` - payload vacio
   - Esperado: `400`, `code=GAM-400`.
3. `POST /achievements/unlock` - unlock duplicado mismo evento
   - Esperado: `200`, `achievementUnlocked=false`, mensaje con `FA-02`.
4. `GET /achievements` - usuario con perfil
   - Esperado: `200`, gallery completa.
5. `GET /achievements` - usuario inexistente
   - Esperado: `404`, `code=GAM-404`.

### C. Subject Progress

1. `POST /subjects/progress` - carga valida
   - Esperado: `200`, `subjects` con datos calculados.
2. `POST /subjects/progress` - lista vacia
   - Esperado: `400` por validacion DTO (`@NotEmpty`) o `404` de negocio si llegara vacia al servicio.
3. `POST /subjects/progress` - subject sin `subjectId`
   - Esperado: `400`, `code=GAM-400`.
4. `GET /subjects/progress` - usuario con snapshots
   - Esperado: `200`, overview.
5. `GET /subjects/progress` - usuario sin snapshots
   - Esperado: `404`, `code=GAM-405`.
6. `GET /subjects/{subjectId}/progress` - subject existente
   - Esperado: `200`, item del subject.
7. `GET /subjects/{subjectId}/progress` - subject inexistente
   - Esperado: `404`, `code=GAM-406`.

## 4) Orden recomendado de ejecucion

1. Ejecutar `POST /points/events` valido para crear perfil.
2. Validar `GET /points`.
3. Ejecutar `POST /achievements/unlock` valido y luego duplicado.
4. Validar `GET /achievements`.
5. Ejecutar `POST /subjects/progress` valido.
6. Validar `GET /subjects/progress` y `GET /subjects/{subjectId}/progress`.
7. Ejecutar negativos al final (payload invalido, usuarios inexistentes, etc.).
