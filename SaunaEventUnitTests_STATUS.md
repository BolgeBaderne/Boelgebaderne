# ✅ Unit Tests - Kørte Succesfuldt

## 📊 Test Resultat
```
Tests run: 14, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## 🧪 Test Coverage

### SaunaEventServiceTest (13 tests) ✅

#### ✅ Oprettelse af Event
- `testCreateEvent_WithValidData_Success()` - Opret event med valid data
- `testCreateEvent_WithNegativeCapacity_ThrowsException()` - Negativ kapacitet → IllegalArgumentException
- `testCreateEvent_WithNegativePrice_ThrowsException()` - Negativ pris → IllegalArgumentException  
- `testCreateEvent_WithEmptyTitle_ThrowsException()` - Tom titel → IllegalArgumentException

#### ✅ Læsning af Events
- `testGetAllEvents_Success()` - Hent alle events
- `testGetById_EventExists_Success()` - Hent event by ID (success)
- `testGetById_EventNotFound_ThrowsException()` - Event findes ikke → EventNotFoundException

#### ✅ Opdatering af Event
- `testUpdateEvent_Success()` - Opdater event med valid data
- `testUpdateEvent_EventNotFound_ThrowsException()` - Opdater non-existent event → EventNotFoundException

#### ✅ Sletning af Event
- `testDeleteEvent_Success()` - Slet event succesfuldt
- `testDeleteEvent_EventNotFound_ThrowsException()` - Slet non-existent event → EventNotFoundException

#### ✅ Business Logic
- `testCalculateAvailableSpots()` - Beregn ledige pladser korrekt
- `testFullyBookedEvent_NoAvailableSpots()` - Fully booked har 0 ledige pladser

---

## 🚀 Kør Tests

```bash
# Kør alle tests
./mvnw test

# Se detaljeret output
./mvnw test -Dsurefire.useFile=false
```

---

## 📋 Krav Opfyldt

| Krav | Status | Test |
|------|--------|------|
| Oprettelse med valid data | ✅ | `testCreateEvent_WithValidData_Success` |
| Invalid data (kapacitet = -1) | ✅ | `testCreateEvent_WithNegativeCapacity_ThrowsException` |
| Invalid data (negativ pris) | ✅ | `testCreateEvent_WithNegativePrice_ThrowsException` |
| Invalid data (tom titel) | ✅ | `testCreateEvent_WithEmptyTitle_ThrowsException` |
| Opdatering af event | ✅ | `testUpdateEvent_Success` |
| Sletning af event | ✅ | `testDeleteEvent_Success` |
| Service layer logic | ✅ | Alle service tests |

---

## 📁 Test Fil
`src/test/java/com/example/bolgebaderne/service/SaunaEventServiceTest.java`

**Alle krav er opfyldt!** 🎉

