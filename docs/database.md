# Database (MongoDB) — lưu ở đâu & cách xem

Hệ thống dùng **1 MongoDB server**, nhưng tách **database theo service** (đúng yêu cầu EDA).

## 1) Các database đang dùng

- `user_service_db`
- `movie_service_db`
- `booking_service_db`
- (payment-service không bắt buộc lưu DB; hiện worker chỉ publish/consume event)

## 2) Khi chạy bằng Docker Compose (khuyến nghị)

### DB lưu ở đâu?

MongoDB chạy trong container `movie-ticket-mongo`.
Dữ liệu thật được lưu trong Docker **volume** tên `mongo_data`.

- Nếu bạn xóa container mà **không** xóa volume, dữ liệu vẫn còn.
- Nếu bạn xóa volume `mongo_data`, dữ liệu sẽ mất (reset sạch).

### Kiểm tra volume đang mount

```powershell
docker volume ls
docker volume inspect mongo_data
```

Trên Windows + Docker Desktop (WSL2), file dữ liệu nằm trong môi trường Linux nội bộ (không nên truy cập trực tiếp bằng Explorer). Hãy thao tác qua `docker` hoặc dùng MongoDB Compass.

### Xem dữ liệu nhanh bằng mongosh trong container

```powershell
docker exec -it movie-ticket-mongo mongosh
```

Trong mongosh:

```javascript
show dbs
use movie_service_db
show collections
db.movies.find().limit(5)
```

### Reset DB (xóa sạch dữ liệu)

Dừng stack rồi xóa volume:

```powershell
docker compose -f infra/docker-compose.yml down
docker volume rm mongo_data
```

Nếu bạn đang chạy infra-only:

```powershell
docker compose -f infra/docker-compose.infra.yml down
docker volume rm mongo_data
```

## 3) Khi chạy MongoDB local (không Docker)

Nếu bạn tự cài MongoDB và chạy `mongod` trên Windows:

- Dữ liệu nằm ở `dbPath` của MongoDB.
- Mặc định thường là `C:\data\db` (tùy cách cài).

Bạn có thể kiểm tra bằng config hoặc service settings của MongoDB trên máy.

## 4) Services trỏ DB như thế nào?

Các service đọc Mongo URI từ env var `MONGODB_URI`:

- user-service: `mongodb://<host>:27017/user_service_db`
- movie-service: `mongodb://<host>:27017/movie_service_db`
- booking-service: `mongodb://<host>:27017/booking_service_db`

Khi chạy LAN 3 máy:

- `<host>` chính là IP của Máy 1 (nơi chạy MongoDB).

## 5) Mở bằng MongoDB Compass

Connection string:

- Local: `mongodb://localhost:27017`
- LAN: `mongodb://<M1_IP>:27017`

Sau khi connect, bạn sẽ thấy các database theo service và collections:

- `users` (user-service)
- `movies` (movie-service)
- `bookings` (booking-service)
