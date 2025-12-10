package com.example.bolgebaderne.dto;

import java.time.LocalDateTime;

public record BookingResponseDTO(
        int eventId,
        String title,
        LocalDateTime startTime,
        int durationMinutes,
        int capacity,
        int remainingSeats,
        double price,
        String status
) {}
