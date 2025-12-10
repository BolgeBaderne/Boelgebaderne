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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
}
