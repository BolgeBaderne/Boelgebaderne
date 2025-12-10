package com.example.bolgebaderne.dto;

public record EventDTO(Long id,
                       String gusmesterNameName,
                       String gusmesterImageUrl,
                       String description,
                       int durationMinutes,
                       int capacity,
                       double price,
                       int currentBookings,
                       int availableSpots)
{}