# Hotel Booking Microservices (Spring Boot + Cloud + JWT)

**Сервисы:** Eureka, Gateway, Booking, Hotel.  
**Фишки:** JWT (HMAC), H2 in-memory, идемпотентность по `requestId`, сага HOLD→CONFIRM/RELEASE, рекомендации номеров по
`timesBooked` + проверка пересечений дат, Resilience4j (Retry/Timeout/CircuitBreaker).  
**Документация:** Swagger на сервисах, `requests.http` для IntelliJ.

## Запуск в IntelliJ IDEA (без терминала)

1. Открой проект: **File → Open…** → папка `hotel-booking-microservices` → **Trust**.
2. SDK: Temurin JDK **21**. Плагин **Lombok** + включи *annotation processing*.
3. Создай конфигурации запуска (зелёная ▶ возле классов):
    - `EurekaServerApplication`
    - `ApiGatewayApplication`
    - `HotelServiceApplication`
    - `BookingServiceApplication`
      Поставь **Allow parallel run** и сделай **Compound** конфигурацию с ними (назови *All Services*).
4. Запусти *All Services*.

## Запуск в терминале

```bash
brew install temurin21 maven
java -version && mvn -v
chmod +x run-all.sh && ./run-all.sh
```

## Адреса

- Eureka:           http://localhost:8761
- Booking Swagger:  http://localhost:8081/swagger-ui.html
- Hotel Swagger:    http://localhost:8082/swagger-ui.html
- API Gateway:      http://localhost:8080

## Быстрый сценарий через Gateway (curl)

```bash
curl -s -X POST http://localhost:8080/auth/register -H 'content-type: application/json'   -d '{"username":"user1","password":"pass"}' | tee token.json

TOKEN=$(sed -n 's/.*"access_token":"\([^"]*\)".*/\1/p' token.json)

curl -s -H "Authorization: Bearer $TOKEN"   "http://localhost:8080/api/rooms/recommend?start=2025-10-26&end=2025-10-28"

curl -s -X POST http://localhost:8080/booking   -H "Authorization: Bearer $TOKEN" -H 'content-type: application/json'   -d '{"roomId":1,"startDate":"2025-10-26","endDate":"2025-10-28","autoSelect":false,"requestId":"req-42"}'
```

## Замечания по ТЗ

- **Разделение доступа**: `@PreAuthorize` на ручках, JWT проверяется в каждом сервисе и на Gateway.
- **Алгоритм планирования**: рекомендации сортируются по `timesBooked`, при равенстве — по `id`; учитываются пересечения
  дат из таблицы удержаний/броней.
- **Сага и согласованность**: `PENDING → CONFIRMED` при успехе, иначе компенсация и `CANCELLED`. Вызовы Hotel обёрнуты в
  Resilience4j (повторы/тайм-аут/брейкер).
- **Идемпотентность**: `requestId` в Booking (уникальный индекс), в Hotel удержания уникальны по `(roomId, requestId)`.
- **INTERNAL-ручки** Hotel под `/internal/rooms/**` — не публикуются через Gateway (Gateway проксирует только
  `/api/...`).

## Тесты

- `booking-service`: позитивный сценарий + идемпотентность, негативный (срыв и компенсация) с моканым `HotelClient`.
- `hotel-service`: конфликт при попытке удержать тот же диапазон дат (409).

