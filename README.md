# UberReviewService

A Spring Boot backend that models the core of a ride-hailing platform — passengers, drivers, bookings with a ride-status state machine, and post-ride reviews.

---

## Features

- **Passengers** — create, fetch, list, update, delete passenger profiles.
- **Drivers** — create, fetch, list, update, delete driver profiles; unique licence numbers enforced.
- **Bookings** — create a booking (auto-assigns an available driver to a passenger); fetch a booking or list all bookings; update ride status through a controlled state machine.
- **Booking status state machine** — enforces valid transitions only:
`ASSIGNED_DRIVER → CAB_ARRIVED → STARTED → IN_RIDE → COMPLETED`, with `CANCELED` allowed from `ASSIGNED_DRIVER`, `CAB_ARRIVED`, or `STARTED`.
- **Reviews** — passengers can leave a rating + written review for a completed booking; one review per booking; fetch a review by ID or by booking ID.
- **Schema versioning** — database schema is managed with Flyway migrations rather than `ddl-auto=update`, so schema changes are explicit and repeatable.
- **Centralized error handling** — consistent JSON error responses for not-found, conflict, and invalid-state-transition failures.
- **Auditing** — `createdDate` / `lastUpdatedDate` automatically maintained on every entity.

---

## Tech Stack

| Layer             | Technology                              |
| ----------------- | ---------------------------------------- |
| Language          | Java 17                                 |
| Framework         | Spring Boot 4.1.0                       |
| Web               | Spring Web (MVC)                        |
| Persistence       | Spring Data JPA / Hibernate             |
| Database          | MySQL                                   |
| Schema migrations | Flyway                                  |
| Boilerplate       | Lombok                                  |
| Build Tool        | Gradle (wrapper included)               |
| Testing           | JUnit 5 (via Spring Boot test starters); manual REST testing via Postman |

---

## Project Structure

```
src/main/java/org/example/uberreviewservice/
├── UberReviewServiceApplication.java   # Application entry point
├── advice/
│   └── GlobalExceptionHandler.java     # Centralized exception -> HTTP response mapping
├── controller/
│   ├── PassengerController.java        # /api/v1/passengers endpoints
│   ├── DriverController.java           # /api/v1/drivers endpoints
│   ├── BookingController.java          # /api/v1/bookings endpoints
│   └── ReviewController.java           # /api/v1/reviews endpoints
├── dto/
│   ├── passenger/                      # Request/response DTOs for passengers
│   ├── driver/                         # Request/response DTOs for drivers
│   ├── booking/                        # Request/response/status-update DTOs for bookings
│   ├── review/                         # Request/response/summary DTOs for reviews
│   └── error/                          # Shared error response DTO
├── exception/                          # Custom domain exceptions
├── mapper/                             # Entity <-> DTO mapping (manual mappers)
├── model/                              # JPA entities (Passenger, Driver, Booking, Review, PassengerReview, BaseModel)
├── repository/                         # Spring Data JPA repositories
└── service/                            # Service interfaces + implementations

src/main/resources/
├── application.properties
└── db/migration/                       # Flyway SQL migrations (V1, V2, V3...)
```

---

## Data Model

- **BaseModel** — abstract superclass providing `id`, `createdDate`, `lastUpdatedDate` to all entities.
- **Passenger** — has many `Booking`s.
- **Driver** — has many `Booking`s; `licenceNumber` is unique.
- **Booking** — links a `Passenger` and a `Driver`; tracks `startTime`, `endTime`, `totalDistance`, and a `bookingStatus`.
- **Review** (base, table `booking_review`) — one-to-one with `Booking`; holds `description` and `rating`.
- **PassengerReview** (extends `Review`, joined-table inheritance) — adds `passengerReviewContent` and `passengerRating`, representing a passenger's review of a completed ride.

---

## Prerequisites

- JDK 17+
- MySQL Server (running locally or reachable)
- Gradle is not required to be installed separately — the repo includes the Gradle Wrapper (`gradlew` / `gradlew.bat`)

---

## Setup & Running Locally

### 1. Clone the repository

```
git clone https://github.com/Abhilash-Panja/UberReviewService.git
cd UberReviewService
```

### 2. Create the database

```
CREATE DATABASE uberdb;
```

Flyway will create and version all tables automatically on startup — no manual schema setup needed beyond creating the empty database.

### 3. Configure the datasource

Update `src/main/resources/application.properties` with your local MySQL credentials if they differ from the defaults:

```
spring.application.name=UberReviewService
spring.datasource.url=jdbc:mysql://localhost:3306/uberdb
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect

spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
```
> **Note:** `ddl-auto=validate` means Hibernate will check the schema matches the entities but won't modify it — all schema changes must go through a new Flyway migration file. Also, plaintext DB credentials here are fine for local dev only; move them to environment variables before deploying anywhere shared.

### 4. Run the application

```
# macOS / Linux
./gradlew bootRun

# Windows
gradlew.bat bootRun
```

The API will be available at `http://localhost:8080`.

### 5. Run tests

```
./gradlew test
```

---

## API Reference

Base path: `/api/v1`

### Passengers — `/api/v1/passengers`

| Method | Endpoint | Description            |
| ------ | -------- | ----------------------- |
| POST   | `/`      | Create a new passenger |
| GET    | `/{id}`  | Get passenger by ID    |
| GET    | `/`      | List all passengers    |
| PUT    | `/{id}`  | Update a passenger     |
| DELETE | `/{id}`  | Delete a passenger — blocked with `409 Conflict` if the passenger has active bookings |

### Drivers — `/api/v1/drivers`

| Method | Endpoint | Description         |
| ------ | -------- | -------------------- |
| POST   | `/`      | Create a new driver |
| GET    | `/{id}`  | Get driver by ID    |
| GET    | `/`      | List all drivers    |
| PUT    | `/{id}`  | Update a driver     |
| DELETE | `/{id}`  | Delete a driver — blocked with `409 Conflict` if the driver has active bookings |

### Bookings — `/api/v1/bookings`

| Method | Endpoint       | Description                                                         |
| ------ | -------------- | --------------------------------------------------------------------- |
| POST   | `/`            | Create a booking for a passenger (auto-assigns an available driver) |
| GET    | `/{id}`        | Get a booking by ID                                                  |
| GET    | `/`            | List all bookings                                                    |
| PATCH  | `/{id}/status` | Update a booking's status (validated against the state machine). Body: `{ "newStatus": "CAB_ARRIVED" }` |

**Booking status flow:**

```
ASSIGNED_DRIVER → CAB_ARRIVED → STARTED → IN_RIDE → COMPLETED
        │              │           │
        └──────────────┴───────────┴──> CANCELED
```

Both forward skips (e.g. `ASSIGNED_DRIVER → COMPLETED`) and backward transitions (e.g. `IN_RIDE → STARTED`) are rejected with `409 Conflict`.

### Reviews — `/api/v1/reviews`

| Method | Endpoint               | Description                             |
| ------ | ----------------------- | ----------------------------------------- |
| POST   | `/`                    | Create a review for a completed booking |
| GET    | `/{id}`                | Get a review by ID                      |
| GET    | `/booking/{bookingId}` | Get the review for a specific booking   |

---

## Error Response Format

All handled exceptions return a consistent JSON shape via `ErrorResponseDTO`, e.g.:

```
{
  "timestamp": "2026-08-02T10:15:30",
  "status": 404,
  "error": "Not Found",
  "message": "Driver not found with id: 1",
  "path": "/api/v1/drivers/1"
}
```

| Exception                                 | Actual HTTP Status |
| ------------------------------------------- | -------------------- |
| `PassengerNotFoundException`              | 404 Not Found       |
| `DriverNotFoundException`                 | 404 Not Found       |
| `BookingNotFoundException`                | 404 Not Found       |
| `ReviewNotFoundException`                 | 404 Not Found       |
| `NoDriversAvailableException`             | 409 Conflict / 503  |
| `DuplicateLicenceNumberException`         | 409 Conflict        |
| `ReviewAlreadyExistsException`            | 409 Conflict        |
| `InvalidBookingStatusTransitionException` | 409 Conflict        |
| `InvalidBookingStateForReviewException`   | 409 Conflict        |
| `PassengerHasActiveBookingsException`     | 409 Conflict        |
| `DriverHasActiveBookingsException`        | 409 Conflict        |
| `MethodArgumentNotValidException` (e.g. missing `newStatus`) | 400 Bad Request |

> Note: `InvalidBookingStatusTransitionException` and `InvalidBookingStateForReviewException` are handled as `409 Conflict` in the current implementation (verified via manual testing), not `400 Bad Request` as an earlier draft of this table stated.

---

## API Testing

The full REST API was manually tested end-to-end with Postman, following the entity dependency (foreign key) order: **Driver / Passenger → Booking → Booking Status → Review**. This ensures every request has the parent data it needs before it's sent, and lets negative tests intentionally violate that order to confirm the API fails safely and predictably.

See [`POSTMAN_TESTING_GUIDE.md`](./POSTMAN_TESTING_GUIDE.md) for the exact request order, request bodies, and expected responses (positive and negative cases) for every endpoint.

### Known Issues Found & Fixed During Testing

Three defects were identified through manual FK-order testing and have since been fixed:

| # | Endpoint | Issue | Fix |
| --- | --- | --- | --- |
| 1 | `PATCH /bookings/{id}/status` | A missing or misnamed `newStatus` field caused an unhandled `NullPointerException` (`500`) instead of a validation error | Added `@NotNull` on `BookingStatusUpdateDTO.newStatus` and a `MethodArgumentNotValidException` handler in `GlobalExceptionHandler` — now returns a clean `400 Bad Request` |
| 2 | `DELETE /passengers/{id}` | Deleting a passenger with existing bookings threw an unhandled `DataIntegrityViolationException` (`500`) from the underlying MySQL FK constraint | Added `PassengerHasActiveBookingsException` with an explicit `countByPassengerId` pre-check — now returns `409 Conflict` with a descriptive message |
| 3 | `DELETE /drivers/{id}` | Same root cause as #2, via the `fk_booking_driver` constraint | Added `DriverHasActiveBookingsException` with an explicit `countByDriverId` pre-check — now returns `409 Conflict` |

---

## Roadmap / Planned Features

- **Geospatial location matching** — track live driver/passenger coordinates and assign the *nearest* available driver instead of the first one found (likely via MySQL spatial types or a geo-indexing library).
- **Authentication** — Spring Security + JWT for passenger/driver identity instead of trusting request bodies directly.
- **Fare calculation** — pricing based on distance, time, and surge conditions.
- **API documentation** — springdoc-openapi (Swagger UI) for interactive API docs.
- **Dockerization** — containerize the app and MySQL for one-command local setup.
- **Pagination** — for `getAllBookings`, `getAllDrivers`, `getAllPassengers` as data grows.

---

## License

*No license added yet. Add a `LICENSE` file (e.g. MIT) here if you want others to be able to use or contribute to this project.*
