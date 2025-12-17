package com.example.bolgebaderne.service;

import com.example.bolgebaderne.dto.AvailableTimeSlotDTO;
import com.example.bolgebaderne.dto.CreateBookingRequest;
import com.example.bolgebaderne.exceptions.NotMemberEligibleException;
import com.example.bolgebaderne.exceptions.TimeSlotFullException;
import com.example.bolgebaderne.model.*;
import com.example.bolgebaderne.repository.BookingRepository;
import com.example.bolgebaderne.repository.SaunaEventRepository;
import com.example.bolgebaderne.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private SaunaEventRepository saunaEventRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingService bookingService;

    private User memberUser;
    private User nonMemberUser;
    private SaunaEvent memberEvent;
    private SaunaEvent guestEvent;
    private SaunaEvent vagtEvent;

    @BeforeEach
    void setUp() {
        // Setup member user
        memberUser = new User();
        memberUser.setUserId(1);
        memberUser.setName("Member User");
        memberUser.setEmail("member@test.com");
        memberUser.setRole(Role.MEMBER);
        memberUser.setPasswordHash("hash123");
        memberUser.setMembershipStatus("ACTIVE");

        // Setup non-member user
        nonMemberUser = new User();
        nonMemberUser.setUserId(2);
        nonMemberUser.setName("Guest User");
        nonMemberUser.setEmail("guest@test.com");
        nonMemberUser.setRole(Role.NON_MEMBER);
        nonMemberUser.setPasswordHash("hash456");
        nonMemberUser.setMembershipStatus("INACTIVE");

        // Setup member event
        memberEvent = new SaunaEvent(
                1,
                "MEDLEM åbent • 07:00-11:00",
                "Members only event",
                "Gusmester",
                LocalDateTime.of(2025, 12, 20, 7, 0),
                240,
                12,
                0.0,
                EventStatus.UPCOMING
        );

        // Setup guest event
        guestEvent = new SaunaEvent(
                2,
                "GÆST åbent • 09:00-10:00",
                "Public event",
                "Gusmester",
                LocalDateTime.of(2025, 12, 20, 9, 0),
                60,
                12,
                80.0,
                EventStatus.UPCOMING
        );

        // Setup vagt event
        vagtEvent = new SaunaEvent(
                3,
                "VAGT • 20:00-21:00",
                "Shift event",
                "Gusmester",
                LocalDateTime.of(2025, 12, 20, 20, 0),
                60,
                12,
                0.0,
                EventStatus.UPCOMING
        );
    }

    // ==================== Tests for getAvailableSlots ====================

    @Test
    void testGetAvailableSlots_MemberUser_CanSeeMemberSlots() {
        // Arrange
        when(userRepository.findById(1)).thenReturn(Optional.of(memberUser));
        when(saunaEventRepository.findAll()).thenReturn(List.of(memberEvent, guestEvent));
        when(bookingRepository.countByEvent_EventId(1)).thenReturn(5L);
        when(bookingRepository.countByEvent_EventId(2)).thenReturn(3L);

        // Act
        List<AvailableTimeSlotDTO> slots = bookingService.getAvailableSlots(1);

        // Assert
        assertNotNull(slots);
        assertEquals(2, slots.size());

        // Check member event
        AvailableTimeSlotDTO memberSlot = slots.get(0);
        assertTrue(memberSlot.userAllowed()); // Member can access member event
        assertEquals(7, memberSlot.available());
        assertEquals(5, memberSlot.booked());

        // Check guest event
        AvailableTimeSlotDTO guestSlot = slots.get(1);
        assertTrue(guestSlot.userAllowed()); // Member can access guest event
        assertEquals(9, guestSlot.available());
        assertEquals(3, guestSlot.booked());
    }

    @Test
    void testGetAvailableSlots_NonMemberUser_CannotSeeMemberSlots() {
        // Arrange
        when(userRepository.findById(2)).thenReturn(Optional.of(nonMemberUser));
        when(saunaEventRepository.findAll()).thenReturn(List.of(memberEvent, guestEvent));
        when(bookingRepository.countByEvent_EventId(1)).thenReturn(5L);
        when(bookingRepository.countByEvent_EventId(2)).thenReturn(3L);

        // Act
        List<AvailableTimeSlotDTO> slots = bookingService.getAvailableSlots(2);

        // Assert
        assertNotNull(slots);
        assertEquals(2, slots.size());

        // Check member event
        AvailableTimeSlotDTO memberSlot = slots.get(0);
        assertFalse(memberSlot.userAllowed()); // Non-member cannot access member event

        // Check guest event
        AvailableTimeSlotDTO guestSlot = slots.get(1);
        assertTrue(guestSlot.userAllowed()); // Non-member can access guest event
    }

    @Test
    void testGetAvailableSlots_FullEvent_MarkedAsFull() {
        // Arrange
        when(userRepository.findById(1)).thenReturn(Optional.of(memberUser));
        when(saunaEventRepository.findAll()).thenReturn(List.of(memberEvent));
        when(bookingRepository.countByEvent_EventId(1)).thenReturn(12L); // Full capacity

        // Act
        List<AvailableTimeSlotDTO> slots = bookingService.getAvailableSlots(1);

        // Assert
        assertNotNull(slots);
        assertEquals(1, slots.size());
        AvailableTimeSlotDTO slot = slots.get(0);
        assertTrue(slot.full());
        assertEquals(0, slot.available());
    }

    // ==================== Tests for createBooking ====================

    @Test
    void testCreateBooking_SuccessfulBooking_ExistingEvent() {
        // Arrange
        CreateBookingRequest request = new CreateBookingRequest(
                1, // userId
                2, // eventId - match guestEvent
                "GÆST åbent • 09:00-10:00",
                "2025-12-20T09:00",
                12
        );

        when(userRepository.findById(1)).thenReturn(Optional.of(memberUser));
        when(saunaEventRepository.findById(2)).thenReturn(Optional.of(guestEvent));
        when(bookingRepository.existsByUser_UserIdAndEvent_EventId(1, 2)).thenReturn(false);
        when(bookingRepository.countByEvent_EventId(2)).thenReturn(5L);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        Booking booking = bookingService.createBooking(request);

        // Assert
        assertNotNull(booking);
        assertEquals(memberUser, booking.getUser());
        assertEquals(guestEvent, booking.getEvent());
        assertEquals(BookingStatus.ACTIVE, booking.getStatus());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void testCreateBooking_CreatesNewEvent_WhenNotFound() {
        // Arrange
        CreateBookingRequest request = new CreateBookingRequest(
                1,
                999, // Non-existing event
                "Offentlig åbent",
                "2025-12-25T10:00",
                12
        );

        SaunaEvent newEvent = new SaunaEvent(
                100,
                "Offentlig åbent",
                "",
                "",
                LocalDateTime.parse("2025-12-25T10:00"),
                60,
                12,
                80.0,
                EventStatus.UPCOMING
        );

        when(userRepository.findById(1)).thenReturn(Optional.of(memberUser));
        when(saunaEventRepository.findById(999)).thenReturn(Optional.empty());
        when(saunaEventRepository.findByTitleAndStartTime(anyString(), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        when(saunaEventRepository.save(any(SaunaEvent.class))).thenReturn(newEvent);
        when(bookingRepository.existsByUser_UserIdAndEvent_EventId(anyInt(), anyInt())).thenReturn(false);
        when(bookingRepository.countByEvent_EventId(anyInt())).thenReturn(0L);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        Booking booking = bookingService.createBooking(request);

        // Assert
        assertNotNull(booking);
        verify(saunaEventRepository).save(any(SaunaEvent.class));
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void testCreateBooking_ThrowsException_WhenNonMemberTriesToBookMemberEvent() {
        // Arrange
        CreateBookingRequest request = new CreateBookingRequest(
                2, // Non-member user
                1,
                "MEDLEM åbent • 07:00-11:00",
                "2025-12-20T07:00",
                12
        );

        when(userRepository.findById(2)).thenReturn(Optional.of(nonMemberUser));
        when(saunaEventRepository.findById(1)).thenReturn(Optional.of(memberEvent));

        // Act & Assert
        assertThrows(NotMemberEligibleException.class, () ->
            bookingService.createBooking(request)
        );

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void testCreateBooking_ThrowsException_WhenNonMemberTriesToBookVagtEvent() {
        // Arrange
        CreateBookingRequest request = new CreateBookingRequest(
                2, // Non-member user
                3,
                "VAGT • 20:00-21:00",
                "2025-12-20T20:00",
                12
        );

        when(userRepository.findById(2)).thenReturn(Optional.of(nonMemberUser));
        when(saunaEventRepository.findById(3)).thenReturn(Optional.of(vagtEvent));

        // Act & Assert
        assertThrows(NotMemberEligibleException.class, () ->
            bookingService.createBooking(request)
        );
    }

    @Test
    void testCreateBooking_ThrowsException_WhenEventIsFull() {
        // Arrange
        CreateBookingRequest request = new CreateBookingRequest(
                1,
                2,
                "GÆST åbent • 09:00-10:00",
                "2025-12-20T09:00",
                12
        );

        when(userRepository.findById(1)).thenReturn(Optional.of(memberUser));
        when(saunaEventRepository.findById(2)).thenReturn(Optional.of(guestEvent));
        when(bookingRepository.existsByUser_UserIdAndEvent_EventId(1, 2)).thenReturn(false);
        when(bookingRepository.countByEvent_EventId(2)).thenReturn(12L); // Full capacity

        // Act & Assert
        assertThrows(TimeSlotFullException.class, () ->
            bookingService.createBooking(request)
        );

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void testCreateBooking_ThrowsException_WhenUserAlreadyBooked() {
        // Arrange
        CreateBookingRequest request = new CreateBookingRequest(
                1,
                2,
                "GÆST åbent • 09:00-10:00",
                "2025-12-20T09:00",
                12
        );

        when(userRepository.findById(1)).thenReturn(Optional.of(memberUser));
        when(saunaEventRepository.findById(2)).thenReturn(Optional.of(guestEvent));
        when(bookingRepository.existsByUser_UserIdAndEvent_EventId(1, 2)).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
            bookingService.createBooking(request)
        );

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    // ==================== Tests for generateWeeklySchedule ====================

    @Test
    void testGenerateWeeklySchedule_MemberUser_GeneratesCorrectSchedule() {
        // Arrange
        LocalDate monday = LocalDate.of(2025, 12, 15); // A Monday
        when(userRepository.findById(1)).thenReturn(Optional.of(memberUser));
        when(saunaEventRepository.findByTitleAndStartTime(anyString(), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        // Act
        List<AvailableTimeSlotDTO> slots = bookingService.generateWeeklySchedule(1, monday);

        // Assert
        assertNotNull(slots);
        assertFalse(slots.isEmpty());

        // Verify member can access member slots
        long memberSlots = slots.stream()
                .filter(slot -> slot.title().contains("Medlems") && slot.userAllowed())
                .count();
        assertTrue(memberSlots > 0);
    }

    @Test
    void testGenerateWeeklySchedule_NonMemberUser_CannotAccessMemberSlots() {
        // Arrange
        LocalDate monday = LocalDate.of(2025, 12, 15);
        when(userRepository.findById(2)).thenReturn(Optional.of(nonMemberUser));
        when(saunaEventRepository.findByTitleAndStartTime(anyString(), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        // Act
        List<AvailableTimeSlotDTO> slots = bookingService.generateWeeklySchedule(2, monday);

        // Assert
        assertNotNull(slots);

        // Verify non-member cannot access Medlemsgus slots
        long restrictedSlots = slots.stream()
                .filter(slot -> slot.title().contains("Medlemsgus") && !slot.userAllowed())
                .count();
        assertTrue(restrictedSlots > 0);

        // Verify non-member can access public slots
        long publicSlots = slots.stream()
                .filter(slot -> slot.title().contains("Offentlig") && slot.userAllowed())
                .count();
        assertTrue(publicSlots > 0);
    }

    @Test
    void testGenerateWeeklySchedule_Wednesday_HasSpecialSchedule() {
        // Arrange
        LocalDate wednesday = LocalDate.of(2025, 12, 17); // A Wednesday
        LocalDate monday = wednesday.minusDays(2);

        when(userRepository.findById(1)).thenReturn(Optional.of(memberUser));
        when(saunaEventRepository.findByTitleAndStartTime(anyString(), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        // Act
        List<AvailableTimeSlotDTO> slots = bookingService.generateWeeklySchedule(1, monday);

        // Assert
        // Wednesday should have public hours and Gæste-gus
        long wednesdaySlots = slots.stream()
                .filter(slot -> slot.startTime().contains("2025-12-17"))
                .count();
        assertTrue(wednesdaySlots > 0);

        // Should have Gæste-gus on Wednesday at 20:00
        boolean hasGaesteGus = slots.stream()
                .anyMatch(slot -> slot.title().contains("Gæste-gus")
                        && slot.startTime().contains("2025-12-17T20:00"));
        assertTrue(hasGaesteGus);
    }

    @Test
    void testGenerateWeeklySchedule_Weekend_HasPublicHours() {
        // Arrange
        LocalDate monday = LocalDate.of(2025, 12, 15);
        when(userRepository.findById(1)).thenReturn(Optional.of(memberUser));
        when(saunaEventRepository.findByTitleAndStartTime(anyString(), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        // Act
        List<AvailableTimeSlotDTO> slots = bookingService.generateWeeklySchedule(1, monday);

        // Assert
        // Saturday (12/20) should have public hours 11-15
        long saturdaySlots = slots.stream()
                .filter(slot -> slot.startTime().contains("2025-12-20")
                        && slot.title().contains("Offentlig"))
                .count();
        assertTrue(saturdaySlots >= 4); // At least 4 hours (11, 12, 13, 14)

        // Sunday (12/21) should have public hours 11-15
        long sundaySlots = slots.stream()
                .filter(slot -> slot.startTime().contains("2025-12-21")
                        && slot.title().contains("Offentlig"))
                .count();
        assertTrue(sundaySlots >= 4);
    }

    @Test
    void testGenerateWeeklySchedule_WithExistingBookings_ShowsCorrectAvailability() {
        // Arrange
        LocalDate monday = LocalDate.of(2025, 12, 15);
        SaunaEvent existingEvent = new SaunaEvent(
                10,
                "Offentlig åbent • 11:00-12:00",
                "",
                "",
                LocalDateTime.of(2025, 12, 20, 11, 0),
                60,
                12,
                80.0,
                EventStatus.UPCOMING
        );

        when(userRepository.findById(1)).thenReturn(Optional.of(memberUser));
        when(saunaEventRepository.findByTitleAndStartTime(anyString(), any(LocalDateTime.class)))
                .thenAnswer(invocation -> {
                    String title = invocation.getArgument(0);
                    LocalDateTime time = invocation.getArgument(1);
                    if (title.equals("Offentlig åbent • 11:00-12:00") &&
                        time.equals(LocalDateTime.of(2025, 12, 20, 11, 0))) {
                        return Optional.of(existingEvent);
                    }
                    return Optional.empty();
                });
        when(bookingRepository.countByEvent_EventId(10)).thenReturn(8L);

        // Act
        List<AvailableTimeSlotDTO> slots = bookingService.generateWeeklySchedule(1, monday);

        // Assert
        Optional<AvailableTimeSlotDTO> bookedSlot = slots.stream()
                .filter(slot -> slot.eventId() == 10)
                .findFirst();

        assertTrue(bookedSlot.isPresent());
        assertEquals(8, bookedSlot.get().booked());
        assertEquals(4, bookedSlot.get().available());
        assertFalse(bookedSlot.get().full());
    }
}

