# Run on 3 machines (LAN) — team-of-3 setup

Goal: each main component runs on a different machine, but the business flow is still asynchronous via RabbitMQ.

## Topology (recommended)

Machine 1 (Gateway + Frontend + Infra)

- MongoDB (Docker)
- RabbitMQ (Docker)
- Gateway (Spring Cloud Gateway)
- Frontend (Vite dev server)

Machine 2 (User + Movie)

- user-service
- movie-service

Machine 3 (Booking + Payment + Notification)

- booking-service
- payment-service (worker)
- notification-worker (worker)

## Prerequisites

- All machines: JDK 17 (for Spring Boot), network access between machines
- Machine 1: Docker Desktop for Mongo/RabbitMQ
- Machine 1 (frontend): Node.js 18+

## Step 1 — Start Infra on Machine 1

From repo root on Machine 1:

```bash
docker compose -f infra/docker-compose.infra.yml up
```

RabbitMQ UI:

- http://<M1_IP>:15672 (guest/guest)

## Step 2 — Start Gateway on Machine 1

Set service URLs to point to Machine 2/3 services:

PowerShell example:

```powershell
$env:USER_SERVICE_URL = "http://<M2_IP>:8081"
$env:MOVIE_SERVICE_URL = "http://<M2_IP>:8082"
$env:BOOKING_SERVICE_URL = "http://<M3_IP>:8083"
./mvnw.ps1 -pl gateway -am spring-boot:run
```

Gateway will be reachable at:

- http://<M1_IP>:8080

## Step 3 — Start user-service + movie-service on Machine 2

Make them connect to Mongo/RabbitMQ on Machine 1:

```powershell
$env:MONGODB_URI = "mongodb://<M1_IP>:27017/user_service_db"
$env:RABBITMQ_HOST = "<M1_IP>"
./mvnw.ps1 -pl user-service -am spring-boot:run
```

```powershell
$env:MONGODB_URI = "mongodb://<M1_IP>:27017/movie_service_db"
./mvnw.ps1 -pl movie-service -am spring-boot:run
```

## Step 4 — Start booking-service + workers on Machine 3

Connect to Mongo/RabbitMQ on Machine 1:

```powershell
$env:MONGODB_URI = "mongodb://<M1_IP>:27017/booking_service_db"
$env:RABBITMQ_HOST = "<M1_IP>"
./mvnw.ps1 -pl booking-service -am spring-boot:run
```

```powershell
$env:RABBITMQ_HOST = "<M1_IP>"
./mvnw.ps1 -pl payment-service -am spring-boot:run
```

```powershell
$env:RABBITMQ_HOST = "<M1_IP>"
./mvnw.ps1 -pl notification-worker -am spring-boot:run
```

## Step 5 — Start Frontend on Machine 1

Point frontend to Gateway:

PowerShell:

```powershell
cd frontend
$env:VITE_API_BASE_URL = "http://<M1_IP>:8080/api"
npm install
npm run dev
```

Windows CMD:

```bat
cd frontend
set VITE_API_BASE_URL=http://<M1_IP>:8080/api
npm install
npm run dev
```

Open from any machine:

- http://<M1_IP>:3000

## Firewall notes

Make sure Windows Firewall allows inbound TCP on:

- Machine 1: 27017, 5672, 15672, 8080, 3000
- Machine 2: 8081, 8082
- Machine 3: 8083

## Quick demo checklist

- Register via Gateway: `POST http://<M1_IP>:8080/api/users/register`
- List movies via Gateway: `GET http://<M1_IP>:8080/api/movies`
- Create booking via Gateway: `POST http://<M1_IP>:8080/api/bookings`
- Watch logs on Machine 3:
  - `payment-service` prints publish SUCCESS/FAILED
  - `notification-worker` prints notification
  - `booking-service` updates booking status
