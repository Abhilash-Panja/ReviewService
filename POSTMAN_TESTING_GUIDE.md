# UberReviewService — Postman API Testing Guide

This guide documents the exact request order used to test the `UberReviewService` REST API, following the entity dependency (foreign key) chain: **Driver / Passenger → Booking → Booking Status → Review**. Each request builds on data created by a previous one, so the order below **must be followed** — sending a child request before its parent exists will fail the FK/state constraint.

Base URL: `http://localhost:8080/api/v1`

---

## Why this order

| Entity | Depends on | Notes |
|---|---|---|
| `Passenger` | — | Independent, create first |
| `Driver` | — | Independent, create first; `licenceNumber` must be unique |
| `Booking` | `Passenger` (FK), `Driver` (auto-assigned) | Fails if passenger doesn't exist or no driver is available |
| `Booking.status` | `Booking` | Must follow the state machine, one step at a time |
| `Review` | `Booking` (FK) | Booking must be `COMPLETED`; only one review per booking |

---

## 1. Create a Driver

```
POST /drivers
```
```json
{
  "driverName": "Ravi Kumar",
  "licenceNumber": "DL1420110012345"
}
```
**Expected:** `201 Created` — returns driver with generated `id`.

---

## 2. Create a Passenger

```
POST /passengers
```
```json
{
  "passengerName": "Anita Sharma"
}
```
**Expected:** `201 Created` — returns passenger with generated `id`.

---

## 3. Create a Booking (positive test)

```
POST /bookings
```
```json
{
  "passengerId": 1
}
```
**Expected:** `201 Created`. Driver is auto-assigned. `bookingStatus` starts as `ASSIGNED_DRIVER`.

**Sample response:**
```json
{
  "id": 1,
  "startTime": null,
  "endTime": null,
  "totalDistance": 0,
  "bookingStatus": "ASSIGNED_DRIVER",
  "driver": { "id": 1, "driverName": "Ravi Kumar" },
  "passenger": { "id": 1, "passengerName": "Anita Sharma" },
  "review": null
}
```

### 3a. Negative test — invalid passengerId

```json
{ "passengerId": 9999 }
```
**Expected:** `404 Not Found` — `PassengerNotFoundException`.

### 3b. Negative test — missing passengerId

```json
{}
```
**Expected:** `400 Bad Request` — validation failure before the FK check runs.

---

## 4. Update Booking Status (state machine, step by step)

```
PATCH /bookings/1/status
```
```json
{ "newStatus": "CAB_ARRIVED" }
```
**Expected:** `200 OK`, `bookingStatus` → `CAB_ARRIVED`.

Repeat sequentially — **do not skip steps**:
```json
{ "newStatus": "STARTED" }
```
```json
{ "newStatus": "IN_RIDE" }
```
```json
{ "newStatus": "COMPLETED" }
```
Each call returns `200 OK` with the updated status, provided the transition is the *immediate next* state.

> ⚠️ Note on field name: the correct JSON key is **`newStatus`**, not `bookingStatus` or `status`. Sending the wrong key silently deserializes to `null` (see defect #1 below).

### 4a. Negative test — invalid transition (backward)

From `IN_RIDE`, attempt:
```json
{ "newStatus": "STARTED" }
```
**Expected:** `409 Conflict` — `InvalidBookingStatusTransitionException`
> `"Cannot transition booking from IN_RIDE to STARTED"`

### 4b. Negative test — invalid transition (forward skip)

On a **fresh** booking still in `ASSIGNED_DRIVER`, attempt:
```json
{ "newStatus": "COMPLETED" }
```
**Expected:** `409 Conflict` — `InvalidBookingStatusTransitionException`
> `"Cannot transition booking from ASSIGNED_DRIVER to COMPLETED"`

### 4c. Negative test — missing newStatus

```json
{}
```
**Expected:** `400 Bad Request`
> `"newStatus: newStatus is required"`
> *(Originally returned an unhandled 500 — see defect #1 below.)*

---

## 5. Create a Review (only once Booking is COMPLETED)

```
POST /reviews
```
```json
{
  "bookingId": 1,
  "description": "Smooth and comfortable ride",
  "rating": 5,
  "passengerReviewContent": "Driver was polite and drove safely",
  "passengerRating": 5
}
```
**Expected:** `201 Created`.

**Sample response:**
```json
{
  "id": 2,
  "passengerRating": 5.0,
  "passengerReviewContent": "Driver was polite and drove safely",
  "bookingId": 1,
  "passengerId": 1,
  "passengerName": "Anita Sharma"
}
```

### 5a. Negative test — duplicate review

Send the exact same request again.
**Expected:** `409 Conflict` — `ReviewAlreadyExistsException`
> `"A review already exists for booking id: 1"`

### 5b. Negative test — review on a non-completed booking

Create a second booking, leave it at `ASSIGNED_DRIVER`, then:
```json
{ "bookingId": 3, "description": "...", "rating": 3, "passengerReviewContent": "Should fail", "passengerRating": 3 }
```
**Expected:** `409 Conflict` — `InvalidBookingStateForReviewException`
> `"Cannot review a booking with status ASSIGNED_DRIVER. Only COMPLETED bookings can be reviewed."`
> *(README documents this as 400 — actual observed behavior is 409; treat the README table as slightly stale.)*

---

## 6. Delete with active FK references

```
DELETE /passengers/1
```
**Expected (after fix):** `409 Conflict`
> `"Cannot delete passenger with id: 1. 2 existing booking(s) reference this passenger."`
> *(Originally returned an unhandled 500 — see defect #2 below.)*

```
DELETE /drivers/1
```
**Expected (after fix):** `409 Conflict`
> `"Cannot delete driver with id: 1. 2 existing booking(s) reference this driver."`
> *(Originally returned an unhandled 500 — see defect #3 below.)*

---

## 7. Duplicate Driver Licence Number

```
POST /drivers
```
```json
{
  "driverName": "Duplicate Test Driver",
  "licenceNumber": "DL1420110012345"
}
```
**Expected:** `409 Conflict` — `DuplicateLicenceNumberException`
> `"A driver with licence number 'DL1420110012345' already exists"`

---

## 8. List Endpoints (sanity check)

```
GET /bookings
GET /passengers
GET /drivers
```
**Expected:** `200 OK`, JSON arrays, no serialization errors on nested driver/passenger/review objects.

---

## Defects Found & Fixed During This Test Cycle

| # | Endpoint | Trigger | Before | After |
|---|---|---|---|---|
| 1 | `PATCH /bookings/{id}/status` | Missing/wrong-named status field | `500` unhandled `NullPointerException` | `400 Bad Request` — added `@NotNull` on `BookingStatusUpdateDTO.newStatus` + `MethodArgumentNotValidException` handler |
| 2 | `DELETE /passengers/{id}` | Passenger has active bookings | `500` unhandled `DataIntegrityViolationException` | `409 Conflict` — added `PassengerHasActiveBookingsException` + pre-check via `countByPassengerId` |
| 3 | `DELETE /drivers/{id}` | Driver has active bookings | `500` unhandled `DataIntegrityViolationException` | `409 Conflict` — added `DriverHasActiveBookingsException` + pre-check via `countByDriverId` |

**Root cause pattern:** `GlobalExceptionHandler` handled its own custom domain exceptions correctly from the start, but had no handler for framework-level exceptions (`DataIntegrityViolationException`) and no input validation guarding a `null` enum before it reached business logic. Both classes of failure surfaced only because testing followed the actual FK/dependency order rather than testing endpoints in isolation.

---

## Postman Setup Tips

- Use **collection variables** (`passengerId`, `driverId`, `bookingId`, `reviewId`) and set them via a `Tests` script on each `POST`:
  ```javascript
  const res = pm.response.json();
  pm.collectionVariables.set("bookingId", res.id);
  ```
- Reference them in later request bodies/URLs as `{{bookingId}}`, etc., so the whole chain replays without manual copy-paste.
- Group requests into Postman **folders** matching the sections above (Drivers → Passengers → Bookings → Status → Reviews → Deletes → Duplicates → Lists) so the collection can be run top-to-bottom with the Collection Runner.