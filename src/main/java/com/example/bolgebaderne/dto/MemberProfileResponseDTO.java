package com.example.bolgebaderne.dto;

public record MemberProfileResponseDTO(
        int userId,
        String name,
        String email,
        String membershipStatus,
        String role
) {}