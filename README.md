# Movie Ticket System (Event-Driven Architecture)

Backend: Spring Boot + MongoDB + RabbitMQ  
Gateway: Spring Cloud Gateway

Core rule (Event-Driven): Booking does NOT call Payment via REST. Payment flow is handled asynchronously via RabbitMQ events.

EDA guard (recommended before demo):

```powershell
./scripts/verify-eda.ps1
```

## 1) Ports

- Gateway: 8080
- User Service: 8081
- Movie Service: 8082
- Booking Service: 8083
- Payment Service (worker): 8084 (no public HTTP API)
- Notification Worker: 8086 (no public HTTP API)
- MongoDB: 27017
- RabbitMQ: 5672
- RabbitMQ UI: 15672

## 2) RabbitMQ Topology

- Exchange: `movie.ticket.exchange` (direct)
- Routing keys:
  - `user.registered`
  - `booking.created`
  - `payment.completed`
  - `booking.failed`
- Queues:
  - `payment.booking.created.queue`
  - `booking.payment.completed.queue`
  - `booking.booking.failed.queue`
  - `notification.payment.completed.queue`
  - `notification.booking.failed.queue`

## 3) Run option A — Full Docker Compose (recommended)

Prerequisite: Docker Desktop is running.

From repo root:

```bash
docker compose -f infra/docker-compose.yml up --build
```

RabbitMQ UI:

- http://localhost:15672 (guest/guest)

## 4) Run option B — Run infra in Docker, run services locally

If you don’t want to containerize services, run only MongoDB + RabbitMQ:

```bash
docker compose -f infra/docker-compose.infra.yml up
```

### 4.1 Use the bundled Maven wrapper (no Maven install required)

- Windows CMD: `mvnw.cmd`
- PowerShell: `./mvnw.ps1`

Example build:

```bash
./mvnw.ps1 -DskipTests package
```

### 4.2 Start each service

Open multiple terminals from repo root and run:

Gateway:

```bash
./mvnw.ps1 -pl gateway -am spring-boot:run
```

User service:

```bash
./mvnw.ps1 -pl user-service -am spring-boot:run
```

Movie service:

```bash
./mvnw.ps1 -pl movie-service -am spring-boot:run
```

Booking service:

```bash
./mvnw.ps1 -pl booking-service -am spring-boot:run
```

Payment worker:

```bash
./mvnw.ps1 -pl payment-service -am spring-boot:run
```

Notification worker:

```bash
./mvnw.ps1 -pl notification-worker -am spring-boot:run
```

## 5) Minimal APIs (via Gateway)

Base URL: `http://localhost:8080`

User:

- POST `/api/users/register`
- POST `/api/users/login`

Movie:

- GET `/api/movies`
- POST `/api/movies`
- PUT `/api/movies/{id}`

Booking:

- POST `/api/bookings`
- GET `/api/bookings`
- GET `/api/bookings/{id}`

## 5.1) Frontend

The frontend is in `frontend/` and calls Gateway only.

Run dev server:

```bash
cd frontend
npm install
npm run dev
```

Optional env var:

- `VITE_API_BASE_URL` (default: `http://localhost:8080/api`)

## 6) Demo flow (end-to-end)

1. Register user
2. List movies
3. Create booking (status will be `PENDING_PAYMENT`)
4. Payment worker consumes `BOOKING_CREATED` and randomly publishes SUCCESS/FAILED
5. Booking service consumes payment result and updates booking status to `CONFIRMED` or `FAILED`
6. Notification worker logs the result

### Example: create booking

POST `http://localhost:8080/api/bookings`

```json
{
  "userId": "<userId>",
  "movieId": "<movieId>",
  "seatNumber": "A5",
  "amount": 90000
}
```

## 7) LAN notes

All services already read Rabbit/Mongo via env vars:

- `RABBITMQ_HOST`
- `MONGODB_URI`

So when running on multiple machines, set those env vars to the machine hosting Mongo/RabbitMQ.

For a complete 3-machine setup (team-of-3), see: `docs/lan-3machines.md`.

### 7.1) 3 people (3 machines) quickstart

Each person clones the repo and runs only their assigned part:

- Person 1 (Machine 1): infra + gateway + frontend
  - `docs/person1-frontend-gateway.md`
- Person 2 (Machine 2): user-service + movie-service
  - `docs/person2-user-movie.md`
- Person 3 (Machine 3): booking-service + payment-service(worker) + notification-worker(worker)
  - `docs/person3-booking-payment-notification.md`

Tip: share IPs first (M1_IP, M2_IP, M3_IP) and open Windows Firewall ports as listed in the guides.

Per-person quick guides:

- `docs/person1-frontend-gateway.md`
- `docs/person2-user-movie.md`
- `docs/person3-booking-payment-notification.md`

Database guide:

- `docs/database.md`
