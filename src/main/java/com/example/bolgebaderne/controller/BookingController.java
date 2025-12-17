package com.example.bolgebaderne.controller;

import com.example.bolgebaderne.dto.AvailableTimeSlotDTO;
import com.example.bolgebaderne.dto.CreateBookingRequest;
import com.example.bolgebaderne.model.Booking;
import com.example.bolgebaderne.service.BookingService;
import org.springframework.web.bind.annotation.*;

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
    public List<AvailableTimeSlotDTO> getAvailable(@RequestParam int userId) {
        return bookingService.getAvailableSlots(userId);
    }

    @PostMapping
    public Booking create(@RequestBody CreateBookingRequest request) {
        return bookingService.createBooking(request);
    }

    // NYT: Generér ugeskema dynamisk ud fra reglerne
    @GetMapping("/week")
    public List<AvailableTimeSlotDTO> getWeek(@RequestParam int userId, @RequestParam String weekStart) {
        LocalDate start = LocalDate.parse(weekStart);   // fx "2025-12-08"
        return bookingService.generateWeeklySchedule(userId, start);
    }
}
