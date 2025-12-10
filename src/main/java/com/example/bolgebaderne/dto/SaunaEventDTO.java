package com.example.bolgebaderne.dto;

import java.time.LocalDateTime;

public record SaunaEventDTO(
        int id,
        String title,
        String saunagusMasterName,
        String saunagusMasterImageUrl,
        String description,
        LocalDateTime startTime,
        int durationMinutes,
        int capacity,
        double price,
        int currentBookings,
        int availableSpots,
        String status
) {}
