package com.example.bolgebaderne.dto;

import java.time.LocalTime;

public record SaunaAdminEventDTO(
        String title,
        String saunagusMasterName,
        String saunagusMasterImageUrl,
        String description,
        LocalTime start_time,   // matcher "HH:mm" fra HTML/JS
        int durationMinutes,
        int capacity,
        double price,
        String status
) {}
