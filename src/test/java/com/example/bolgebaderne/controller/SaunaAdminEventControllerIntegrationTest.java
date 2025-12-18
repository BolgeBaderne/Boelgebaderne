package com.example.bolgebaderne.controller;

import com.example.bolgebaderne.dto.SaunaAdminEventDTO;
import com.example.bolgebaderne.dto.SaunaEventDTO;
import com.example.bolgebaderne.model.EventStatus;
import com.example.bolgebaderne.model.SaunaEvent;
import com.example.bolgebaderne.repository.BookingRepository;
import com.example.bolgebaderne.repository.SaunaEventRepository;
import com.example.bolgebaderne.repository.UserRepository;
import com.example.bolgebaderne.repository.WaitlistEntryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("SaunaAdminEventController Integration Tests")
class SaunaAdminEventControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private SaunaAdminEventDTO testEventDTO;
    private LocalDateTime testStartTime;

    // Store actual IDs
    private Integer event1Id;
    private Integer event2Id;

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private SaunaEventRepository repository;
    @Autowired
    private WaitlistEntryRepository waitlistEntryRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        waitlistEntryRepository.deleteAll();
        repository.deleteAll();
        userRepository.deleteAll();

        testStartTime = LocalDateTime.of(2025, 12, 25, 14, 0);

        // CREATE TEST EVENTS and capture their IDs
        SaunaEvent event1 = new SaunaEvent();
        event1.setTitle("Test Event 1");
        event1.setGusmesterName("Test Gusmester 1");
        event1.setGusmesterImageUrl("https://example.com/test1.jpg");
        event1.setDescription("Test beskrivelse 1");
        event1.setStartTime(testStartTime);
        event1.setDurationMinutes(60);
        event1.setCapacity(15);
        event1.setPrice(150.0);
        event1.setEventStatus(EventStatus.UPCOMING);
        event1.setCurrentBookings(0);
        SaunaEvent savedEvent1 = repository.save(event1);
        event1Id = savedEvent1.getEventId(); // Capture the actual ID

        SaunaEvent event2 = new SaunaEvent();
        event2.setTitle("Test Event 2");
        event2.setGusmesterName("Test Gusmester 2");
        event2.setGusmesterImageUrl("https://example.com/test2.jpg");
        event2.setDescription("Test beskrivelse 2");
        event2.setStartTime(testStartTime. plusDays(1));
        event2.setDurationMinutes(90);
        event2.setCapacity(20);
        event2.setPrice(200.0);
        event2.setEventStatus(EventStatus. UPCOMING);
        event2.setCurrentBookings(0);
        SaunaEvent savedEvent2 = repository.save(event2);
        event2Id = savedEvent2.getEventId(); // Capture the actual ID

        testEventDTO = new SaunaAdminEventDTO(
                "Integration Test Event",
                "Test Gusmester",
                "https://example.com/test.jpg",
                "Integration test beskrivelse",
                testStartTime,
                60,
                15,
                150.0,
                "UPCOMING"
        );
    }

    // ===== ADMIN ADGANG TESTS =====

    @Test
    @DisplayName("ADMIN kan kalde GET /api/admin/events - 200 OK")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetAllEvents_AsAdmin_Returns200() throws Exception {
        mockMvc.perform(get("/api/admin/events"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("ADMIN kan kalde GET /api/admin/events/{id} - 200 OK")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetEventById_AsAdmin_Returns200() throws Exception {
        mockMvc.perform(get("/api/admin/events/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("ADMIN kan kalde POST /api/admin/events - 200 OK")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreateEvent_AsAdmin_Returns200() throws Exception {
        mockMvc.perform(post("/api/admin/events")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testEventDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Integration Test Event"))
                .andExpect(jsonPath("$.gusmesterName").value("Test Gusmester"));
    }

    @Test
    @DisplayName("ADMIN kan kalde PUT /api/admin/events/{id} - 200 OK")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdateEvent_AsAdmin_Returns200() throws Exception {
        SaunaAdminEventDTO updateDTO = new SaunaAdminEventDTO(
                "Opdateret Event",
                "Opdateret Gusmester",
                "https://example.com/updated.jpg",
                "Opdateret beskrivelse",
                testStartTime,
                90,
                20,
                200.0,
                "UPCOMING"
        );

        mockMvc.perform(put("/api/admin/events/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Opdateret Event"))
                .andExpect(jsonPath("$.gusmesterName").value("Opdateret Gusmester"));
    }

    @Test
    @DisplayName("ADMIN kan kalde DELETE /api/admin/events/{id} - 200 OK")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteEvent_AsAdmin_Returns200() throws Exception {
        mockMvc.perform(delete("/api/admin/events/1")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    // ===== MEMBER ADGANG TESTS (403 FORBIDDEN) =====

    @Test
    @DisplayName("MEMBER kan IKKE kalde GET /api/admin/events - 302 eller 403")
    @WithMockUser(username = "member", roles = {"MEMBER"})
    void testGetAllEvents_AsMember_Returns403() throws Exception {
        mockMvc.perform(get("/api/admin/events"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("MEMBER kan IKKE kalde GET /api/admin/events/{id} - 302 eller 403")
    @WithMockUser(username = "member", roles = {"MEMBER"})
    void testGetEventById_AsMember_Returns403() throws Exception {
        mockMvc.perform(get("/api/admin/events/1"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("MEMBER kan IKKE kalde POST /api/admin/events - 302 eller 403")
    @WithMockUser(username = "member", roles = {"MEMBER"})
    void testCreateEvent_AsMember_Returns403() throws Exception {
        mockMvc.perform(post("/api/admin/events")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testEventDTO)))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("MEMBER kan IKKE kalde PUT /api/admin/events/{id} - 302 eller 403")
    @WithMockUser(username = "member", roles = {"MEMBER"})
    void testUpdateEvent_AsMember_Returns403() throws Exception {
        mockMvc.perform(put("/api/admin/events/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testEventDTO)))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("MEMBER kan IKKE kalde DELETE /api/admin/events/{id} - 302 eller 403")
    @WithMockUser(username = "member", roles = {"MEMBER"})
    void testDeleteEvent_AsMember_Returns403() throws Exception {
        mockMvc.perform(delete("/api/admin/events/1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    // ===== NON_MEMBER ADGANG TESTS (403 FORBIDDEN) =====

    @Test
    @DisplayName("NON_MEMBER kan IKKE kalde GET /api/admin/events - 302 eller 403")
    @WithMockUser(username = "nonmember", roles = {"NON_MEMBER"})
    void testGetAllEvents_AsNonMember_Returns403() throws Exception {
        mockMvc.perform(get("/api/admin/events"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("NON_MEMBER kan IKKE kalde GET /api/admin/events/{id} - 302 eller 403")
    @WithMockUser(username = "nonmember", roles = {"NON_MEMBER"})
    void testGetEventById_AsNonMember_Returns403() throws Exception {
        mockMvc.perform(get("/api/admin/events/1"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("NON_MEMBER kan IKKE kalde POST /api/admin/events - 302 eller 403")
    @WithMockUser(username = "nonmember", roles = {"NON_MEMBER"})
    void testCreateEvent_AsNonMember_Returns403() throws Exception {
        mockMvc.perform(post("/api/admin/events")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testEventDTO)))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("NON_MEMBER kan IKKE kalde PUT /api/admin/events/{id} - 302 eller 403")
    @WithMockUser(username = "nonmember", roles = {"NON_MEMBER"})
    void testUpdateEvent_AsNonMember_Returns403() throws Exception {
        mockMvc.perform(put("/api/admin/events/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testEventDTO)))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("NON_MEMBER kan IKKE kalde DELETE /api/admin/events/{id} - 302 eller 403")
    @WithMockUser(username = "nonmember", roles = {"NON_MEMBER"})
    void testDeleteEvent_AsNonMember_Returns403() throws Exception {
        mockMvc.perform(delete("/api/admin/events/1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    // ===== CRUD FLOW TESTS =====

    @Test
    @DisplayName("CRUD Flow: Opret → Hent → Verificér data")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCrudFlow_CreateAndFetch_VerifyData() throws Exception {
        // 1. Opret event
        MvcResult createResult = mockMvc.perform(post("/api/admin/events")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testEventDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String createResponse = createResult.getResponse().getContentAsString();
        SaunaEventDTO createdEvent = objectMapper.readValue(createResponse, SaunaEventDTO.class);

        // Verificér oprettelse
        assertNotNull(createdEvent);
        assertEquals("Integration Test Event", createdEvent.title());
        assertEquals("Test Gusmester", createdEvent.gusmesterName());
        assertEquals(15, createdEvent.capacity());
        assertEquals(150.0, createdEvent.price());

        // 2. Hent event via GET
        MvcResult fetchResult = mockMvc.perform(get("/api/admin/events/" + createdEvent.id()))
                .andExpect(status().isOk())
                .andReturn();

        String fetchResponse = fetchResult.getResponse().getContentAsString();
        SaunaEventDTO fetchedEvent = objectMapper.readValue(fetchResponse, SaunaEventDTO.class);

        // 3. Verificér at hentet data matcher oprettet data
        assertEquals(createdEvent.id(), fetchedEvent.id());
        assertEquals(createdEvent.title(), fetchedEvent.title());
        assertEquals(createdEvent.gusmesterName(), fetchedEvent.gusmesterName());
        assertEquals(createdEvent.capacity(), fetchedEvent.capacity());
        assertEquals(createdEvent.price(), fetchedEvent.price());
    }

    @Test
    @DisplayName("CRUD Flow: Opdater event → Verificér ændringer")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCrudFlow_UpdateEvent_VerifyChanges() throws Exception {
        // 1. Hent original event
        MvcResult originalResult = mockMvc.perform(get("/api/admin/events/1"))
                .andExpect(status().isOk())
                .andReturn();

        String originalResponse = originalResult.getResponse().getContentAsString();
        SaunaEventDTO originalEvent = objectMapper.readValue(originalResponse, SaunaEventDTO.class);

        // 2. Opdater event med nye data
        SaunaAdminEventDTO updateDTO = new SaunaAdminEventDTO(
                "Opdateret Titel",
                "Ny Gusmester",
                "https://example.com/new.jpg",
                "Ny beskrivelse",
                testStartTime,
                120,
                25,
                250.0,
                "UPCOMING"
        );

        MvcResult updateResult = mockMvc.perform(put("/api/admin/events/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String updateResponse = updateResult.getResponse().getContentAsString();
        SaunaEventDTO updatedEvent = objectMapper.readValue(updateResponse, SaunaEventDTO.class);

        // 3. Verificér at ændringerne er gemt
        assertEquals(originalEvent.id(), updatedEvent.id()); // ID skal være det samme
        assertNotEquals(originalEvent.title(), updatedEvent.title());
        assertNotEquals(originalEvent.gusmesterName(), updatedEvent.gusmesterName());
        assertEquals("Opdateret Titel", updatedEvent.title());
        assertEquals("Ny Gusmester", updatedEvent.gusmesterName());
        assertEquals(25, updatedEvent.capacity());
        assertEquals(250.0, updatedEvent.price());

        // 4. Hent event igen for at dobbelttjekke
        MvcResult verifyResult = mockMvc.perform(get("/api/admin/events/1"))
                .andExpect(status().isOk())
                .andReturn();

        String verifyResponse = verifyResult.getResponse().getContentAsString();
        SaunaEventDTO verifiedEvent = objectMapper.readValue(verifyResponse, SaunaEventDTO.class);

        assertEquals(updatedEvent.title(), verifiedEvent.title());
        assertEquals(updatedEvent.gusmesterName(), verifiedEvent.gusmesterName());
    }

    @Test
    @DisplayName("CRUD Flow: Slet event → 200 OK og efterfølgende 404 ved GET")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCrudFlow_DeleteEvent_ThenGetReturns404() throws Exception {
        // 1. Verificér at event eksisterer
        mockMvc.perform(get("/api/admin/events/2"))
                .andExpect(status().isOk());

        // 2. Slet event
        mockMvc.perform(delete("/api/admin/events/2")
                        .with(csrf()))
                .andExpect(status().isOk());

        // 3. Forsøg at hente slettet event → Skal give 404
        mockMvc.perform(get("/api/admin/events/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("CRUD Flow: Komplet flow - Opret → Opdater → Slet → Verificér")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCrudFlow_Complete_CreateUpdateDelete() throws Exception {
        // 1. OPRET event
        MvcResult createResult = mockMvc.perform(post("/api/admin/events")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testEventDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String createResponse = createResult.getResponse().getContentAsString();
        SaunaEventDTO createdEvent = objectMapper.readValue(createResponse, SaunaEventDTO.class);
        int eventId = createdEvent.id();

        // 2. OPDATER event
        SaunaAdminEventDTO updateDTO = new SaunaAdminEventDTO(
                "Opdateret Event",
                "Opdateret Gusmester",
                "https://example.com/updated.jpg",
                "Opdateret beskrivelse",
                testStartTime,
                90,
                20,
                200.0,
                "UPCOMING"
        );

        mockMvc.perform(put("/api/admin/events/" + eventId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Opdateret Event"));

        // 3. Verificér opdatering
        mockMvc.perform(get("/api/admin/events/" + eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Opdateret Event"))
                .andExpect(jsonPath("$.capacity").value(20));

        // 4. SLET event
        mockMvc.perform(delete("/api/admin/events/" + eventId)
                        .with(csrf()))
                .andExpect(status().isOk());

        // 5. Verificér sletning (404)
        mockMvc.perform(get("/api/admin/events/" + eventId))
                .andExpect(status().isNotFound());
    }

    // ===== UNAUTHORIZED ACCESS TEST =====

    @Test
    @DisplayName("Uautentificeret bruger kan IKKE tilgå admin endpoints - 302 Redirect")
    void testUnauthorizedAccess_Returns401() throws Exception {
        mockMvc.perform(get("/api/admin/events"))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(post("/api/admin/events")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testEventDTO)))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(put("/api/admin/events/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testEventDTO)))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(delete("/api/admin/events/1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }
}

