package com.example.bolgebaderne.service;

import com.example.bolgebaderne.dto.AvailableTimeSlotDTO;
import com.example.bolgebaderne.dto.BookingResponseDTO;
import com.example.bolgebaderne.dto.CreateBookingRequest;
import com.example.bolgebaderne.exceptions.NotMemberEligibleException;
import com.example.bolgebaderne.exceptions.TimeSlotFullException;
import com.example.bolgebaderne.model.Booking;
import com.example.bolgebaderne.model.BookingStatus;
import com.example.bolgebaderne.model.SaunaEvent;
import com.example.bolgebaderne.model.User;
import com.example.bolgebaderne.repository.BookingRepository;
import com.example.bolgebaderne.repository.SaunaEventRepository;
import com.example.bolgebaderne.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.example.bolgebaderne.model.EventStatus;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepo;
    private final SaunaEventRepository eventRepo;
    private final UserRepository userRepo;
    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepo,
                          SaunaEventRepository eventRepo,
                          UserRepository userRepo, BookingRepository bookingRepository) {
        this.bookingRepo = bookingRepo;
        this.eventRepo = eventRepo;
        this.userRepo = userRepo;
        this.bookingRepository = bookingRepository;
    }

    public List<AvailableTimeSlotDTO> getAvailableSlots(Integer userId) {

        boolean guestMode = (userId == null);
        boolean member = false;

        if (!guestMode) {
            User user = userRepo.findById(userId).orElseThrow();
            member = user.isMember();
        }

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        final boolean isMemberFinal = member;
        final boolean guestModeFinal = guestMode;


        return eventRepo.findAll().stream().map(event -> {
            long booked = bookingRepo.countBySaunaEvent_EventId(event.getEventId());
            int available = event.getCapacity() - (int) booked;

            boolean userAllowed =
                    (event.getTitle().contains("MEDLEM") && isMemberFinal) ||
                            (event.getTitle().contains("GÆST")) ||
                            (event.getTitle().contains("VAGT") && isMemberFinal);

            if (guestModeFinal) {
                userAllowed = true;
            }



            String startTimeFormatted = "";
            if (event.getStartTime() != null) {
                startTimeFormatted = event.getStartTime().format(fmt);
            }

            return new AvailableTimeSlotDTO(
                    event.getEventId(),
                    event.getTitle(),
                    startTimeFormatted,
                    event.getCapacity(),
                    (int) booked,
                    available,
                    available <= 0,
                    userAllowed
            );
        }).toList();
    }

    public Booking createBooking(CreateBookingRequest req) {

        // Find bruger
        User user = userRepo.findById(req.userId()).orElseThrow();

        // 1) Prøver først at finde et event på det eventId, vi har
        SaunaEvent event = eventRepo.findById(req.eventId()).orElse(null);

        // 2) Hvis det ikke findes
        //    prøver vi at finde et event ud fra titel + starttid
        LocalDateTime start = null;
        if (event == null) {
            start = LocalDateTime.parse(req.startTime());
            event = eventRepo.findByTitleAndStartTime(req.title(), start).orElse(null);
        }

        // 3) Hvis der stadig ikke findes et event, opretter vi et nyt
        if (event == null) {
            if (start == null) {
                start = LocalDateTime.parse(req.startTime());
            }

            // Pris: 80 kr for offentlig åbent, 120 kr for gæste-gus
            double price = 0.0;
            String upper = req.title().toUpperCase();
            if (upper.contains("GÆSTE-GUS")) {
                price = 120.0;
            } else if (upper.contains("OFFENTLIG")) {
                price = 80.0;
            }

            // Brug den offentlige constructor:
//           public SaunaEvent(int eventId, String title, String description, String gusmesterName,
//                    String gusmesterImageUrl, LocalDateTime startTime, int durationMinutes,
//            int capacity, double price, int currentBookings, EventStatus status) {

                SaunaEvent newEvent = new SaunaEvent(
                    0,
                    req.title(),
                    "",
                    "",
                    "",
                    start,
                    60,
                    req.capacity(),
                    price,
                    EventStatus.UPCOMING,
                    0,
                    req.capacity()
            );


            event = eventRepo.save(newEvent);
        }


        boolean member = user.isMember();

        String upperTitle = event.getTitle() == null
                ? ""
                : event.getTitle().toUpperCase();

        // Medlemskrav: MEDLEM, MEDLEMSGUS og VAGT
        if ((upperTitle.contains("MEDLEM")
                || upperTitle.contains("MEDLEMSGUS")
                || upperTitle.contains("VAGT"))
                && !member) {

            throw new NotMemberEligibleException(
                    "Din medlemsstatus giver ikke adgang til denne tid.");
        }

        // Stop hvis brugeren allerede har booket denne tid
        if (bookingRepo.existsByUser_UserIdAndSaunaEvent_EventId(
                user.getUserId(), event.getEventId())) {
            throw new IllegalArgumentException("Du har allerede booket denne tid.");
        }

        long booked = bookingRepo.countBySaunaEvent_EventId(event.getEventId());
        if (booked >= event.getCapacity()) {
            throw new TimeSlotFullException("Denne tid er allerede fuldt booket.");
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setSaunaEvent(event);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setStatus(BookingStatus.ACTIVE);

        return bookingRepo.save(booking);
    }

    public List<AvailableTimeSlotDTO> generateWeeklySchedule(Integer userId, LocalDate weekStart) {

        boolean guestMode = (userId == null);
        boolean isMember = false;

        if (!guestMode) {
            User user = userRepo.findById(userId).orElseThrow();
            isMember = user.isMember();
        }

        List<AvailableTimeSlotDTO> slots = new ArrayList<>();

        int capacity = 10; // max antal personer i saunaen

        // 7 dage: mandag (weekStart) til søndag
        for (int i = 0; i < 7; i++) {
            LocalDate date = weekStart.plusDays(i);
            DayOfWeek day = date.getDayOfWeek();

            // ---------- MEDLEMS-ÅBNINGSTIDER (ALLE DAGE UNDTAGEN ONSDAG) ----------
            if (day != DayOfWeek.WEDNESDAY) {
                // Morgen 07:00–11:00
                slots.add(makeOpenSlot(date.atTime(7, 0), 240, capacity, "Medlems åbent", isMember));
                // Aften 16:00–21:00
                slots.add(makeOpenSlot(date.atTime(16, 0), 300, capacity, "Medlems åbent", isMember));
            }

            // ---------- ONSDAG SÆRLIG ----------
            if (day == DayOfWeek.WEDNESDAY) {
                // Medlemmer kun 07:00–09:00
                slots.add(makeOpenSlot(date.atTime(7, 0), 120, capacity, "Medlems åbent", isMember));

                // Offentlig 09:00–11:00 (2 x 1 time)
                for (int h = 9; h < 11; h++) {
                    slots.add(makeGuestSlot(date.atTime(h, 0), 60, capacity));
                }

                // Offentlig 15:00–21:00 (6 x 1 time)
                for (int h = 15; h < 21; h++) {
                    slots.add(makeGuestSlot(date.atTime(h, 0), 60, capacity));
                }
            }

            // ---------- WEEKEND OFFENTLIG ----------
            // LØRDAG: offentlig 11–15
            if (day == DayOfWeek.SATURDAY) {
                for (int h = 11; h < 15; h++) {
                    slots.add(makeGuestSlot(date.atTime(h, 0), 60, capacity));
                }
            }

            // SØNDAG: præcis det samme som lørdag
            if (day == DayOfWeek.SUNDAY) {
                for (int h = 11; h < 15; h++) {
                    slots.add(makeGuestSlot(date.atTime(h, 0), 60, capacity));
                }
            }

            // ---------- GUS-TIDER ----------
            // Tirsdag 20–21 (medlemsgus)
            if (day == DayOfWeek.TUESDAY) {
                slots.add(makeGusSlot(date.atTime(20, 0), 60, capacity, "Medlemsgus", isMember));
            }

            // Torsdag 17:30–18:30 (medlemsgus)
            if (day == DayOfWeek.THURSDAY) {
                slots.add(makeGusSlot(date.atTime(17, 30), 60, capacity, "Medlemsgus", isMember));
            }

            // Torsdag 20–21 (medlemsgus)
            if (day == DayOfWeek.THURSDAY) {
                slots.add(makeGusSlot(date.atTime(20, 0), 60, capacity, "Medlemsgus", isMember));
            }

            // Fredag 07–08 (medlemsgus)
            if (day == DayOfWeek.FRIDAY) {
                slots.add(makeGusSlot(date.atTime(7, 0), 60, capacity, "Medlemsgus", isMember));
            }

            // Onsdag gæste-gus 20–21
            if (day == DayOfWeek.WEDNESDAY) {
                slots.add(makeGusSlot(date.atTime(20, 0), 60, capacity, "Gæste-gus", isMember));
            }
        }

        return slots;
    }


    // ---------- HJÆLPEMETODER----------
    private AvailableTimeSlotDTO makeOpenSlot(LocalDateTime start, int duration, int capacity, String label, boolean isMember) {
        return new AvailableTimeSlotDTO(999,
                label + " • " + start.toLocalTime() + "-" + start.plusMinutes(duration).toLocalTime(),
                start.toString(),
                capacity,
                0,
                capacity,
                false,
                isMember);
    }

    private AvailableTimeSlotDTO makeGuestSlot(LocalDateTime start, int duration, int capacity) {
        String title = "Offentlig åbent • "
                + start.toLocalTime()
                + "-" + start.plusMinutes(duration).toLocalTime();

        // Forsøg at finde et eksisterende event i DB
        SaunaEvent event = eventRepo.findByTitleAndStartTime(title, start).orElse(null);

        int effectiveCapacity = capacity;
        int booked = 0;
        int eventId = 0;
        boolean full = false;

        if (event != null) {
            eventId = event.getEventId();
            effectiveCapacity = event.getCapacity();
            booked = (int) bookingRepo.countBySaunaEvent_EventId(event.getEventId());
            full = booked >= effectiveCapacity;
        }

        int available = Math.max(effectiveCapacity - booked, 0);

        boolean userAllowed = true;
        return new AvailableTimeSlotDTO(
                eventId,
                title,
                start.toString(),
                effectiveCapacity,
                booked,
                available,
                full,
                true
        );

    }

    private AvailableTimeSlotDTO makeGusSlot(
            LocalDateTime start,
            int duration,
            int capacity,
            String titlePrefix,
            boolean isMember
    ) {
        String title = titlePrefix + " • "
                + start.toLocalTime()
                + "-" + start.plusMinutes(duration).toLocalTime();

        // SlåR op i DB om der allerede findes et event for den her gus
        SaunaEvent event = eventRepo.findByTitleAndStartTime(title, start).orElse(null);

        int effectiveCapacity = capacity;
        int booked = 0;
        int eventId = 0;
        boolean full = false;

        if (event != null) {
            eventId = event.getEventId();
            effectiveCapacity = event.getCapacity();
            booked = (int) bookingRepo.countBySaunaEvent_EventId(event.getEventId());
            full = booked >= effectiveCapacity;
        }

        int available = Math.max(effectiveCapacity - booked, 0);

        boolean userAllowed = true;
        if (titlePrefix != null && titlePrefix.toUpperCase().contains("MEDLEM")) {
            userAllowed = isMember;
        }

        return new AvailableTimeSlotDTO(
                eventId,
                title,
                start.toString(),
                effectiveCapacity,
                booked,
                available,
                full,
                userAllowed
        );

    }

    //Afmelder en booking og frigiver pladsen til første på ventelisten.
    @Transactional
    public void cancelBooking(int bookingId) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + bookingId));

        SaunaEvent event = booking.getSaunaEvent();

        // enten delete eller markér som CANCELLED – her tager vi den simple
        bookingRepo.delete(booking);
    }


    public List<BookingResponseDTO> getBookingsForUser(int userId) {

        List<Booking> bookings = bookingRepo.findByUser_UserId(userId);

        List<BookingResponseDTO> bookingResponses = new ArrayList<>();

        for (Booking booking : bookings) {
            BookingResponseDTO bookingResponseDTO =
                    toBookingResponseDTO(booking);
            bookingResponses.add(bookingResponseDTO);
        }
        return bookingResponses;
    }

    private BookingResponseDTO toBookingResponseDTO(Booking booking) {

        SaunaEvent saunaEvent = booking.getSaunaEvent();

        return new BookingResponseDTO(
                saunaEvent.getEventId(),
                saunaEvent.getTitle(),
                saunaEvent.getStartTime(),
                saunaEvent.getDurationMinutes(),
                saunaEvent.getCapacity(),
                saunaEvent.getAvailableSpots(),
                saunaEvent.getPrice(),
                booking.getStatus().name()
        );
    }
}

