package com.example.bolgebaderne.controller;

import com.example.bolgebaderne.dto.AvailableTimeSlotDTO;
import com.example.bolgebaderne.dto.CreateBookingRequest;
import com.example.bolgebaderne.model.*;
import com.example.bolgebaderne.repository.*;
import com.example.bolgebaderne.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for BookingController.
 * Tester alle lag: Controller -> Service -> Repository -> H2 Database
 * Ingen mocks - alt er rigtigt.
 */
@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.sql.init.mode=always"
})
@Transactional
class BookingControllerIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SaunaEventRepository saunaEventRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User testMember;
    private User testNonMember;
    private SaunaEvent testEvent;

    @BeforeEach
    void setUp() {
        // Ryd data før hver test
        bookingRepository.deleteAll();
        saunaEventRepository.deleteAll();
        userRepository.deleteAll();

        // Opret testbrugere
        testMember = new User();
        testMember.setName("Test Member");
        testMember.setEmail("member@test.dk");
        testMember.setRole(Role.MEMBER);
        testMember.setPasswordHash("hash123");
        testMember.setMembershipStatus("active");
        testMember = userRepository.save(testMember);

        testNonMember = new User();
        testNonMember.setName("Test NonMember");
        testNonMember.setEmail("nonmember@test.dk");
        testNonMember.setRole(Role.NON_MEMBER);
        testNonMember.setPasswordHash("hash456");
        testNonMember.setMembershipStatus("inactive");
        testNonMember = userRepository.save(testNonMember);

        // Opret et test-event
        testEvent = new SaunaEvent(
                0,
                "Offentlig Gus",
                "Test event",
                "Test Gusmester",
                "",
                LocalDateTime.of(2025, 12, 20, 10, 0),
                120, // duration i minutter
                6,
                80.0,
                0,
                EventStatus.UPCOMING
        );
        testEvent = saunaEventRepository.save(testEvent);
    }

    @Test
    void getAvailable_returnsAvailableSlots_fromDatabase() {
        List<AvailableTimeSlotDTO> slots = bookingService.getAvailableSlots(testMember.getUserId());

        assertNotNull(slots);
        assertTrue(slots.size() >= 1);

        AvailableTimeSlotDTO firstSlot = slots.stream()
                .filter(s -> "Offentlig Gus".equals(s.title()))
                .findFirst()
                .orElseThrow();

        assertEquals("Offentlig Gus", firstSlot.title());
        assertEquals(6, firstSlot.capacity());
        assertEquals(0, firstSlot.booked());
    }

    @Test
    void createBooking_persistsToDatabase_andReturnsBooking() {
        CreateBookingRequest request = new CreateBookingRequest(
                testMember.getUserId(),
                testEvent.getEventId(),
                "Offentlig Gus",
                "2025-12-20T10:00:00",
                6
        );

        Booking booking = bookingService.createBooking(request);

        assertNotNull(booking);
        assertNotNull(booking.getBookingId());
        assertEquals(BookingStatus.ACTIVE, booking.getStatus());
        assertEquals(testMember.getUserId(), booking.getUser().getUserId());
        assertEquals(testEvent.getEventId(), booking.getEvent().getEventId());

        // Verificer at booking er i databasen
        long count = bookingRepository.countByEvent_EventId(testEvent.getEventId());
        assertEquals(1, count);
    }

    @Test
    void createBooking_throwsException_whenEventIsFull() {
        // Fyld eventet op
        for (int i = 0; i < testEvent.getCapacity(); i++) {
            Booking booking = new Booking();
            booking.setUser(testMember);
            booking.setEvent(testEvent);
            booking.setStatus(BookingStatus.ACTIVE);
            booking.setCreatedAt(LocalDateTime.now());
            bookingRepository.save(booking);
        }

        CreateBookingRequest request = new CreateBookingRequest(
                testNonMember.getUserId(),
                testEvent.getEventId(),
                "Offentlig Gus",
                "2025-12-20T10:00:00",
                6
        );

        // Forvent exception
        assertThrows(Exception.class, () -> bookingService.createBooking(request));
    }

    @Test
    void createBooking_createsNewEvent_whenEventIdIsZero() {
        CreateBookingRequest request = new CreateBookingRequest(
                testMember.getUserId(),
                0, // Intet eventId -> opretter nyt
                "GÆSTE-GUS",
                "2025-12-22T14:00:00",
                6
        );

        Booking booking = bookingService.createBooking(request);

        assertNotNull(booking);
        assertEquals("GÆSTE-GUS", booking.getEvent().getTitle());
        assertEquals(120.0, booking.getEvent().getPrice());

        // Verificer at nyt event er oprettet i databasen
        long eventCount = saunaEventRepository.count();
        assertEquals(2, eventCount); // testEvent + nyt event
    }

    @Test
    void getWeek_generatesWeeklySchedule_basedOnRules() {
        LocalDate weekStart = LocalDate.of(2025, 12, 22); // Mandag
        List<AvailableTimeSlotDTO> schedule = bookingService.generateWeeklySchedule(testMember.getUserId(), weekStart);

        assertNotNull(schedule);
        assertTrue(schedule.size() > 10, "Ugeskemaet skal have mere end 10 slots");

        // Verificer at der er forskellige typer events
        boolean hasEvents = !schedule.isEmpty();
        assertTrue(hasEvents, "Der skal være mindst ét event i ugen");

        // Verificer at nogle slots har kapacitet
        boolean someHaveCapacity = schedule.stream().anyMatch(s -> s.capacity() > 0);
        assertTrue(someHaveCapacity, "Nogle events skal have kapacitet");
    }

    @Test
    void getAvailable_showsBookedCounts_forExistingBookings() {
        // Opret en booking
        Booking booking = new Booking();
        booking.setUser(testMember);
        booking.setEvent(testEvent);
        booking.setStatus(BookingStatus.ACTIVE);
        booking.setCreatedAt(LocalDateTime.now());
        bookingRepository.save(booking);

        List<AvailableTimeSlotDTO> slots = bookingService.getAvailableSlots(testMember.getUserId());

        AvailableTimeSlotDTO offentligGusSlot = slots.stream()
                .filter(s -> "Offentlig Gus".equals(s.title()))
                .findFirst()
                .orElseThrow();

        assertEquals(1, offentligGusSlot.booked());
    }

    @Test
    void createBooking_preventsDuplicateBooking_forSameUser() {
        CreateBookingRequest request = new CreateBookingRequest(
                testMember.getUserId(),
                testEvent.getEventId(),
                "Offentlig Gus",
                "2025-12-20T10:00:00",
                6
        );

        // Første booking lykkes
        Booking firstBooking = bookingService.createBooking(request);
        assertNotNull(firstBooking);

        // Anden booking skal fejle
        assertThrows(Exception.class, () -> bookingService.createBooking(request));
    }

    @Test
    void createBooking_allowsNonMember_toBookOffentligGus() {
        CreateBookingRequest request = new CreateBookingRequest(
                testNonMember.getUserId(),
                testEvent.getEventId(),
                "Offentlig Gus",
                "2025-12-20T10:00:00",
                6
        );

        Booking booking = bookingService.createBooking(request);

        assertNotNull(booking);
        assertEquals(testNonMember.getUserId(), booking.getUser().getUserId());
    }

    @Test
    void createBooking_blocksNonMember_fromBookingMemberEvent() {
        // Opret medlem-event
        SaunaEvent memberEvent = new SaunaEvent(
                0,
                "MEDLEM-GUS",
                "For medlemmer",
                "Gusmester",
                "",
                LocalDateTime.of(2025, 12, 21, 10, 0),
                120,
                6,
                0.0,
                0,
                EventStatus.UPCOMING
        );
        memberEvent = saunaEventRepository.save(memberEvent);

        CreateBookingRequest request = new CreateBookingRequest(
                testNonMember.getUserId(),
                memberEvent.getEventId(),
                "MEDLEM-GUS",
                "2025-12-21T10:00:00",
                6
        );

        // Forvent exception for ikke-medlem
        assertThrows(Exception.class, () -> bookingService.createBooking(request));
    }
}

