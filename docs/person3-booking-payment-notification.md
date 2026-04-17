# Person 3 — booking-service + payment-service + notification-worker (Machine 3)

You run:

- booking-service (8083)
- payment-service (worker)
- notification-worker (worker)

All connect to MongoDB + RabbitMQ running on Machine 1.

## booking-service

```powershell
$env:MONGODB_URI = "mongodb://<M1_IP>:27017/booking_service_db"
$env:RABBITMQ_HOST = "<M1_IP>"
./mvnw.ps1 -pl booking-service -am spring-boot:run
```

Responsibilities covered:

- `POST /bookings`, `GET /bookings`, `GET /bookings/{id}`
- publish `BOOKING_CREATED` with routing key `booking.created`
- consume payment results:
  - `PAYMENT_COMPLETED` -> set booking status `CONFIRMED`
  - `BOOKING_FAILED` -> set booking status `FAILED`

## payment-service (worker)

```powershell
$env:RABBITMQ_HOST = "<M1_IP>"
./mvnw.ps1 -pl payment-service -am spring-boot:run
```

Responsibilities covered:

- consume `BOOKING_CREATED`
- random success/fail
- publish `PAYMENT_COMPLETED` or `BOOKING_FAILED`

## notification-worker (worker)

```powershell
$env:RABBITMQ_HOST = "<M1_IP>"
./mvnw.ps1 -pl notification-worker -am spring-boot:run
```

Responsibilities covered:

- consume payment result events and log to console

## Firewall

Allow inbound ports on Machine 3:

- 8083
