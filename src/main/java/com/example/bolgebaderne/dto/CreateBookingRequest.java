package com.example.bolgebaderne.dto;

public record CreateBookingRequest(
        int userId,
        int eventId
) {}
