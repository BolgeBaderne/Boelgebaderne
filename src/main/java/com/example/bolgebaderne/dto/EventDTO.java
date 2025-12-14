package com.example.bolgebaderne.dto;

public record EventDTO(
        Long id,
        String title,
        String description,
        String gusmesterName,
        String gusmesterImageUrl,
        String startTime,          // ISO string
        int durationMinutes,
        int capacity,
        double price,
        int currentBookings,
        int availableSpots,
        String status
) {}