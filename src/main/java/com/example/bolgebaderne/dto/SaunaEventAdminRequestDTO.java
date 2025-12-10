package com.example.bolgebaderne.dto;

import java.time.LocalTime;

public record SaunaEventAdminRequestDTO(
        String title,
        String saunagusMasterName,
        String saunagusMasterImageUrl,
        String description,
        LocalTime start_time,
        int durationMinutes,
        int capacity,
        double price,
        String status

) {}
