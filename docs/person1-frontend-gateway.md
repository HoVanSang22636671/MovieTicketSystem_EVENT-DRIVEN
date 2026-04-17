# Person 1 — Frontend + Gateway (Machine 1)

You run:

- MongoDB + RabbitMQ (Docker)
- Gateway
- Frontend

## 1) Start infra (Mongo + RabbitMQ)

From repo root:

```bash
docker compose -f infra/docker-compose.infra.yml up
```

RabbitMQ UI: http://<M1_IP>:15672 (guest/guest)

## 2) Start Gateway

Point Gateway to services on other machines:

```powershell
$env:USER_SERVICE_URL = "http://<M2_IP>:8081"
$env:MOVIE_SERVICE_URL = "http://<M2_IP>:8082"
$env:BOOKING_SERVICE_URL = "http://<M3_IP>:8083"
./mvnw.ps1 -pl gateway -am spring-boot:run
```

Gateway URL:

- http://<M1_IP>:8080

## 3) Start Frontend

Frontend must call Gateway only.

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

Frontend URL:

- http://<M1_IP>:3000

## 4) Firewall

Allow inbound ports on Machine 1:

- 27017, 5672, 15672, 8080, 3000
