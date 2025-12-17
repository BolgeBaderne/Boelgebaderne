package com.example.bolgebaderne.dto;


public record AvailableTimeSlotDTO(
        int eventId,
        String title,
        String startTime,
        int capacity,
        int booked,
        int available,
        boolean full,
        boolean userAllowed
) {}