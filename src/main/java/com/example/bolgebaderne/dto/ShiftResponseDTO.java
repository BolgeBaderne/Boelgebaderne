package com.example.bolgebaderne.dto;

//dummy data bare for at tjekke det ud - udover hvad der står på ER model
import java.time.LocalDate;
import java.time.LocalTime;

public record ShiftResponseDTO(
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        String label
) {}