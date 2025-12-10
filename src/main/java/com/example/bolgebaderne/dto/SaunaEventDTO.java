package com.example.bolgebaderne.dto;

public record SaunaEventDTO(Long id,
                            String saunagusMasterName,
                            String saunagusMasterImageUrl,
                            String description,
                            int durationMinutes,
                            int capacity,
                            double price,
                            int currentBookings,
                            int availableSpots)
{}
