package com.example.bolgebaderne.dto;

import java.time.LocalDateTime;

public record SaunaAdminEventDTO(
        String title,
        String saunagusMasterName,
        String saunagusMasterImageUrl,
        String description,
        LocalDateTime startTime,
        int durationMinutes,
        int capacity,
        double price,
        String status   // "UPCOMING", "FULLY_BOOKED", "CANCELLED"

) {}
