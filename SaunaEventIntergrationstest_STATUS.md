# ✅ Integration Tests - SaunaAdminEventController

Denne README forklarer integration tests for admin event endpoints.

## 📊 Test Resultat
```
Tests run: 18, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## 🎯 Formål

Integration tests tester hele stacken (controller → service → database) for at sikre:
- **Korrekt HTTP respons** (200, 403, 404, 401)
- **Authorization fungerer** (kun ADMIN har adgang)
- **CRUD operationer virker** fra start til slut
- **Data persisteres korrekt** i databasen

## 🧪 Test Coverage

### SaunaAdminEventControllerIntegrationTest (18 tests) ✅

#### ✅ ADMIN Adgang Tests (200 OK)
Verificerer at ADMIN kan udføre alle CRUD operationer:
- `testGetAllEvents_AsAdmin_Returns200()` - GET `/api/admin/events` → 200 OK
- `testGetEventById_AsAdmin_Returns200()` - GET `/api/admin/events/{id}` → 200 OK  
- `testCreateEvent_AsAdmin_Returns201()` - POST `/api/admin/events` → 201 Created
- `testUpdateEvent_AsAdmin_Returns200()` - PUT `/api/admin/events/{id}` → 200 OK
- `testDeleteEvent_AsAdmin_Returns200()` - DELETE `/api/admin/events/{id}` → 200 OK

#### ✅ MEMBER Adgang Tests (403 Forbidden)
Verificerer at MEMBER **IKKE** kan tilgå admin endpoints:
- `testGetAllEvents_AsMember_Returns403()` - GET → 403 Forbidden
- `testGetEventById_AsMember_Returns403()` - GET by ID → 403 Forbidden
- `testCreateEvent_AsMember_Returns403()` - POST → 403 Forbidden
- `testUpdateEvent_AsMember_Returns403()` - PUT → 403 Forbidden
- `testDeleteEvent_AsMember_Returns403()` - DELETE → 403 Forbidden

#### ✅ NON_MEMBER Adgang Tests (403 Forbidden)
Verificerer at NON_MEMBER **IKKE** kan tilgå admin endpoints:
- `testGetAllEvents_AsNonMember_Returns403()` - GET → 403 Forbidden
- `testCreateEvent_AsNonMember_Returns403()` - POST → 403 Forbidden

#### ✅ CRUD Flow Tests
Tester komplette workflows:
- `testCreateAndFetchEvent_Success()` - Opret event → Hent via GET → Verificér data matcher
- `testUpdateAndVerifyEvent_Success()` - Opdater event → Hent igen → Verificér ændringer
- `testDeleteAndVerifyGone_Success()` - Slet event → GET skal returnere 404
- `testCompleteCRUDFlow_Success()` - Opret → Opdater → Slet → Verificér hele flowet

#### ✅ Unauthorized Access Test (401)
- `testGetAllEvents_Unauthorized_Returns401()` - Uautentificeret bruger → 401 Unauthorized

---

## 🔧 Teknologier

- **@SpringBootTest**: Starter fuld Spring context med database
- **@AutoConfigureMockMvc**: Giver adgang til MockMvc for HTTP simulation
- **@WithMockUser**: Simulerer autentificeret bruger med specifikke roller
- **MockMvc**: Sender HTTP requests uden rigtig server
- **ObjectMapper**: Konverterer mellem Java objekter og JSON

---

## 📝 Vigtige Annotationer

```java
@SpringBootTest              // Starter fuld application context
@AutoConfigureMockMvc        // Gør MockMvc tilgængelig
@WithMockUser(roles = {"ADMIN"}) // Simulerer ADMIN bruger
```

---

## 🚀 Kør Tests

```bash
# Kør alle integration tests
./mvnw test -Dtest=SaunaAdminEventControllerIntegrationTest

# Kør specifik test
./mvnw test -Dtest=SaunaAdminEventControllerIntegrationTest#testGetAllEvents_AsAdmin_Returns200

# Se detaljeret output
./mvnw test -Dtest=SaunaAdminEventControllerIntegrationTest -Dsurefire.useFile=false
```

---

## 📊 Test Struktur

```java
@Test
@DisplayName("Beskrivende test navn")
@WithMockUser(roles = {"ADMIN"})
void testMethod() throws Exception {
    mockMvc.perform(get("/api/admin/events"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.title").value("Expected"));
}
```

---

## ⚠️ Forskelle: Unit Tests vs Integration Tests

| Unit Tests | Integration Tests |
|------------|-------------------|
| Mocker dependencies (@MockBean) | Bruger rigtig database (@Autowired) |
| Tester isoleret logik | Tester hele stacken (controller → service → DB) |
| Hurtigere eksekvering | Langsommere (starter Spring context) |
| Fokus på business logic | Fokus på HTTP endpoints + authorization |

---

## 📋 Krav Opfyldt

✅ **Oprettelse af event med valid data** - POST returnerer 201 Created  
✅ **Oprettelse med invalid data** - Exception handling verificeret  
✅ **Opdatering af event** - PUT returnerer 200 OK med opdateret data  
✅ **Sletning af event** - DELETE returnerer 200 OK + verificerer ressource er væk  
✅ **Rollekontrol gennem controller-tests** - ADMIN (200), MEMBER (403), NON_MEMBER (403), Unauthorized (401)

---

## 📌 Best Practices

1. **Brug beskrivende DisplayName** - Gør det nemt at se hvad der testes
2. **Test alle roller** - ADMIN, MEMBER, NON_MEMBER, og unauthorized
3. **Test CRUD flows** - Ikke kun isolerede endpoints
4. **Verificér data** - Brug ObjectMapper til at læse og verificere responses
5. **Cleanup efter tests** - Slet testdata eller brug `@Transactional`
