package com.example.bolgebaderne.controller;

import com.example.bolgebaderne.dto.AvailableTimeSlotDTO;
import com.example.bolgebaderne.dto.CreateBookingRequest;
import com.example.bolgebaderne.model.Booking;
import com.example.bolgebaderne.service.BookingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/available")
    public List<AvailableTimeSlotDTO> getAvailable(@RequestParam int userId) {
        return bookingService.getAvailableSlots(userId);
    }

    @PostMapping
    public Booking create(@RequestBody CreateBookingRequest request) {
        return bookingService.createBooking(request);
    }
}
