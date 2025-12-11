package com.example.bolgebaderne.service;

import com.example.bolgebaderne.model.Booking;
import com.example.bolgebaderne.model.SaunaEvent;
import com.example.bolgebaderne.repository.BookingRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class BookingService {

    private final BookingRepository bookingRepo;
    private final WaitlistEntryService waitlistEntryService;

    public BookingService(BookingRepository bookingRepo,
                          WaitlistEntryService waitlistEntryService) {
        this.bookingRepo = bookingRepo;
        this.waitlistEntryService = waitlistEntryService;
    }


    //Afmelder en booking og frigiver pladsen til første på ventelisten.
    @Transactional
    public void cancelBooking(int bookingId) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + bookingId));

        SaunaEvent event = booking.getSaunaEvent();

        // enten delete eller markér som CANCELLED – her tager vi den simple
        bookingRepo.delete(booking);

        // giv pladsen til første på ventelisten (hvis der er nogen)
        waitlistEntryService.promoteFirstInQueue(event);
    }
}

