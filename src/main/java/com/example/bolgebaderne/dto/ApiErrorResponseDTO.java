package com.example.bolgebaderne.dto;

import java.time.LocalDateTime;

public record ApiErrorResponseDTO(
        int status,
        String error,
        String message,
        String path,
        LocalDateTime timestamp
) {}
