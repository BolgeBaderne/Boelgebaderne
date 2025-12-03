package com.example.bolgebaderne.service;

import com.example.bolgebaderne.dto.BookingResponseDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MemberQuickBookingService {

    public List<BookingResponseDTO> getQuickBookings() {

        LocalDateTime now = LocalDateTime.now();

        return List.of(
                new BookingResponseDTO(
                        1,
                        "Morgengus – Blid Start",
                        now.plusDays(1).withHour(7).withMinute(30),
                        12,    // durationMinutes
                        12,    // capacity
                        5,     // remainingSeats
                        89.0,  // price
                        "UPCOMING"
                ),
                new BookingResponseDTO(
                        2,
                        "Aftengus – Intens Varme",
                        now.plusDays(1).withHour(19).withMinute(0),
                        10,    // durationMinutes
                        10,    // capacity
                        0,     // remainingSeats
                        99.0,  // price
                        "FULLY_BOOKED"
                )
        );
    }
}
