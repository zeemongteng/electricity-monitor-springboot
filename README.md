# Electricity Monitor — Spring Boot

ต้นแบบระบบเก็บบันทึกหน่วยการใช้ไฟฟ้า โดย **Simulation แทนอุปกรณ์จริง** ตามการปรับขอบเขตโครงงาน

## Features
- Simulator ส่งข้อมูลเข้า PostgreSQL อัตโนมัติ
- REST API รับข้อมูลจาก simulator/อุปกรณ์จริงในอนาคต
- เก็บ voltage, current, power และ energy (kWh)
- Daily statistics: total / average / maximum / record count
- ตรวจจับ usage spike และสร้าง notification
- Dashboard HTML
- Docker + PostgreSQL พร้อม deploy

## Architecture
Simulation -> Spring Boot -> PostgreSQL
                         -> Anomaly Detection -> Notifications
                         -> Daily Statistics -> Dashboard

## Anomaly rule
หลังมีข้อมูลย้อนหลังอย่างน้อย 6 records:

`current usage > historical average × ANOMALY_MULTIPLIER`

ค่าเริ่มต้น `1.5` และแก้ได้ผ่าน environment variable

## Run with Docker
```bash
mvn clean package -DskipTests
docker compose up --build
```
เปิด `http://localhost:8080`

## Run without Docker
ต้องมี Java 21 + Maven + PostgreSQL และตั้ง:
```text
DATABASE_URL=jdbc:postgresql://localhost:5432/electricity
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=postgres
```
จากนั้น:
```bash
mvn spring-boot:run
```

## API
```text
GET  /api/health
POST /api/readings
GET  /api/readings?meterId=SIM-001&date=2026-10-01
GET  /api/statistics/daily?meterId=SIM-001&date=2026-10-01
GET  /api/notifications
GET  /api/notifications?unreadOnly=true
PATCH /api/notifications/{id}/read
```

POST example:
```json
{
  "meterId": "SIM-001",
  "voltage": 220.0,
  "current": 1.5,
  "powerWatts": 330.0,
  "energyKwh": 0.08
}
```

## Deploy
ใช้ Docker บน platform ที่รองรับ Docker เช่น Render, Railway, Fly.io หรือ VM ได้

Environment variables:
```text
DATABASE_URL=jdbc:postgresql://<host>:5432/electricity
DATABASE_USERNAME=<username>
DATABASE_PASSWORD=<password>
SIMULATOR_ENABLED=true
SIMULATOR_INTERVAL_MS=10000
ANOMALY_MULTIPLIER=1.5
```

## Push to GitHub
```bash
git init
git add .
git commit -m "Initial electricity monitoring Spring Boot project"
git branch -M main
git remote add origin <YOUR_GITHUB_REPOSITORY_URL>
git push -u origin main
```

## Future extension
- ESP32/PZEM-004T ส่งเข้า `POST /api/readings`
- user / room / meter หลายตัว
- กราฟรายชั่วโมง
- WebSocket/SSE real-time
- Email/LINE notification
- baseline แยกตามช่วงเวลา
