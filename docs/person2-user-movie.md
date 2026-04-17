# Person 2 — user-service + movie-service (Machine 2)

You run:

- user-service (8081)
- movie-service (8082)

Both connect to MongoDB + RabbitMQ running on Machine 1.

## What you need from Person 1

- Machine 1 IP (Mongo/RabbitMQ host): `<M1_IP>`
- You will give back your Machine 2 IP to Person 1: `<M2_IP>`

Person 1 will configure Gateway to call:

- `http://<M2_IP>:8081`
- `http://<M2_IP>:8082`

## One-time: build (optional but recommended)

From repo root:

```powershell
./mvnw.ps1 -DskipTests package
```

## Run services (open 2 terminals)

Terminal A: `user-service`
Terminal B: `movie-service`

## user-service

This service publishes the `USER_REGISTERED` event to RabbitMQ (asynchronous).

```powershell
$env:MONGODB_URI = "mongodb://<M1_IP>:27017/user_service_db"
$env:RABBITMQ_HOST = "<M1_IP>"
./mvnw.ps1 -pl user-service -am spring-boot:run
```

CMD equivalent:

```bat
set MONGODB_URI=mongodb://<M1_IP>:27017/user_service_db
set RABBITMQ_HOST=<M1_IP>
mvnw.cmd -pl user-service -am spring-boot:run
```

Responsibilities covered:

- `POST /register`, `POST /login`
- publish `USER_REGISTERED` to exchange `movie.ticket.exchange` with routing key `user.registered`

## movie-service

```powershell
$env:MONGODB_URI = "mongodb://<M1_IP>:27017/movie_service_db"
./mvnw.ps1 -pl movie-service -am spring-boot:run
```

CMD equivalent:

```bat
set MONGODB_URI=mongodb://<M1_IP>:27017/movie_service_db
mvnw.cmd -pl movie-service -am spring-boot:run
```

Responsibilities covered:

- `GET /movies`, `POST /movies`
- `PUT /movies/{id}`
- seed demo movies on startup

## Quick health checks (from any machine on LAN)

- Direct (bypass gateway):
  - `POST http://<M2_IP>:8081/register`
  - `POST http://<M2_IP>:8081/login`
  - `GET  http://<M2_IP>:8082/movies`

Note: in the real demo, frontend should call Gateway only. These direct calls are just to confirm Person 2’s services are up.

## Firewall

Allow inbound ports on Machine 2:

- 8081, 8082
