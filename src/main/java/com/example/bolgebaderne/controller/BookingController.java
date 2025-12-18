package com.example.bolgebaderne.controller;

import com.example.bolgebaderne.dto.AvailableTimeSlotDTO;
import com.example.bolgebaderne.dto.CreateBookingRequest;
import com.example.bolgebaderne.model.Booking;
import com.example.bolgebaderne.service.BookingService;
import org.springframework.web.bind.annotation.*;
import com.example.bolgebaderne.model.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;


import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/bookings")
public class BookingController
{

    private final BookingService bookingService;

    public BookingController(BookingService bookingService)
    {
        this.bookingService = bookingService;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(@PathVariable int id)
    {
        bookingService.cancelBooking(id);
        return ResponseEntity.noContent().build();
    }

    // Det gamle endpoint (bruger sauna_events-tabellen)
    @GetMapping("/available")
    public List<AvailableTimeSlotDTO> getAvailable(@RequestParam(required = false) Integer userId) {
        return bookingService.getAvailableSlots(userId);
    }

    @PostMapping
    public Booking create(@AuthenticationPrincipal User user,
                          @RequestBody CreateBookingRequest request) {

        if (user == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED,
                    "Du skal være logget ind for at booke."
            );
        }

        // Vi bruger altid den rigtige (loggede ind) bruger-id
        CreateBookingRequest safeRequest = new CreateBookingRequest(
                user.getUserId(),
                request.eventId(),
                request.title(),
                request.startTime(),
                request.capacity()
        );

        return bookingService.createBooking(safeRequest);
    }

    // NYT: Generér ugeskema dynamisk ud fra reglerne
    @GetMapping("/week")
    public List<AvailableTimeSlotDTO> getWeek(@RequestParam(required = false) Integer userId,
                                              @RequestParam String weekStart) {LocalDate start = LocalDate.parse(weekStart);   // fx "2025-12-08"
        return bookingService.generateWeeklySchedule(userId, start);
    }
}
