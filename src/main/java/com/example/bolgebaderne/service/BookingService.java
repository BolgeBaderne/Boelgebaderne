package com.example.bolgebaderne.service;

import com.example.bolgebaderne.dto.AvailableTimeSlotDTO;
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
import org.springframework.stereotype.Service;

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

    public BookingService(BookingRepository bookingRepo,
                          SaunaEventRepository eventRepo,
                          UserRepository userRepo) {
        this.bookingRepo = bookingRepo;
        this.eventRepo = eventRepo;
        this.userRepo = userRepo;
    }

    public List<AvailableTimeSlotDTO> getAvailableSlots(int userId) {

        User user = userRepo.findById(userId).orElseThrow();
        boolean member = user.isMember();

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        return eventRepo.findAll().stream().map(event -> {
            long booked = bookingRepo.countByEvent_EventId(event.getEventId());
            int available = event.getCapacity() - (int) booked;

            boolean userAllowed =
                    (event.getTitle().contains("MEDLEM") && member) ||
                            (event.getTitle().contains("GÆST")) ||
                            (event.getTitle().contains("VAGT") && member);

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

        User user = userRepo.findById(req.userId()).orElseThrow();
        SaunaEvent event = eventRepo.findById(req.eventId()).orElseThrow();

        boolean member = user.isMember();

        if (event.getTitle().contains("MEDLEM") && !member) {
            throw new NotMemberEligibleException("Denne tid er kun for medlemmer.");
        }

        if (event.getTitle().contains("VAGT") && !member) {
            throw new NotMemberEligibleException("Kun medlemmer kan tage en vagt.");
        }

        long booked = bookingRepo.countByEvent_EventId(event.getEventId());
        if (booked >= event.getCapacity()) {
            throw new TimeSlotFullException("Tiden er allerede fuldt booket.");
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setEvent(event);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setStatus(BookingStatus.ACTIVE);


        return bookingRepo.save(booking);
    }
    public List<AvailableTimeSlotDTO> generateWeeklySchedule(int userId, LocalDate weekStart) {

        User user = userRepo.findById(userId).orElseThrow();
        boolean isMember = user.isMember();

        List<AvailableTimeSlotDTO> slots = new ArrayList<>();

        // Sauna kapacitet
        int capacity = 12;

        // Tider vi genererer for hver dag (7 dage)
        for (int i = 0; i < 7; i++) {
            LocalDate date = weekStart.plusDays(i);
            DayOfWeek day = date.getDayOfWeek();

            // ---------- MEDLEMS-ÅBNINGSTIDER ----------
            if (day != DayOfWeek.WEDNESDAY) {
                // Morgen 07:00–11:00
                slots.add(makeOpenSlot(date.atTime(7,0), 240, capacity, "Medlem åbning", isMember));
                // Aften 16:00–21:00
                slots.add(makeOpenSlot(date.atTime(16,0), 300, capacity, "Medlem åbning", isMember));
            }

            // ---------- ONSDAG SÆRLIG ----------
            if (day == DayOfWeek.WEDNESDAY) {
                // Medlemmer kun 07:00–09:00
                slots.add(makeOpenSlot(date.atTime(7,0), 120, capacity, "Medlem åbning", isMember));

                // Offentlig 09:00–11:00
                slots.add(makeGuestSlot(date.atTime(9,0), 120, capacity));

                // Offentlig 15:00–21:00
                slots.add(makeGuestSlot(date.atTime(15,0), 360, capacity));
            }

            // ---------- WEEKEND OFFENTLIG ----------
            if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
                slots.add(makeGuestSlot(date.atTime(11,0), 240, capacity));
            }

            // ---------- GUS-TIDER ----------
            // Tirsdag 20–21
            if (day == DayOfWeek.TUESDAY) {
                slots.add(makeGusSlot(date.atTime(20,0), 60, capacity, "Medlemsgus"));
            }

            // Torsdag 17:30–18:30
            if (day == DayOfWeek.THURSDAY) {
                slots.add(makeGusSlot(date.atTime(17,30), 60, capacity, "Medlemsgus 17:30"));
            }

            // Torsdag 20–21
            if (day == DayOfWeek.THURSDAY) {
                slots.add(makeGusSlot(date.atTime(20,0), 60, capacity, "Medlemsgus"));
            }

            // Fredag 07–08 (gus)
            if (day == DayOfWeek.FRIDAY) {
                slots.add(makeGusSlot(date.atTime(7,0), 60, capacity, "Morgengus"));
            }

            // Onsdag gæste-gus
            if (day == DayOfWeek.WEDNESDAY) {
                slots.add(makeGusSlot(date.atTime(20,0), 60, capacity, "Gæste-gus"));
            }
        }

        return slots;
    }

    // ---------- HJÆLPEMETHODS ----------
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
        return new AvailableTimeSlotDTO(999,
                "Offentlig åbning • " + start.toLocalTime() + "-" + start.plusMinutes(duration).toLocalTime(),
                start.toString(),
                capacity,
                0,
                capacity,
                false,
                true);
    }

    private AvailableTimeSlotDTO makeGusSlot(LocalDateTime start, int duration, int capacity, String title) {
        return new AvailableTimeSlotDTO(999,
                title + " • " + start.toLocalTime() + "-" + start.plusMinutes(duration).toLocalTime(),
                start.toString(),
                capacity,
                0,
                capacity,
                false,
                true);
    }

}
