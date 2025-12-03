package com.example.bolgebaderne.service;

import com.example.bolgebaderne.dto.ShiftResponseDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class MemberShiftService {

    public List<ShiftResponseDTO> getShifts() {
        LocalDate today = LocalDate.now();

        return List.of(
                new ShiftResponseDTO(
                        today.plusDays(1),
                        LocalTime.of(8, 0),
                        LocalTime.of(12, 0),
                        "Morgenvagt"
                ),
                new ShiftResponseDTO(
                        today.plusDays(1),
                        LocalTime.of(12, 0),
                        LocalTime.of(16, 0),
                        "Middag"
                ),
                new ShiftResponseDTO(
                        today.plusDays(3),
                        LocalTime.of(16, 0),
                        LocalTime.of(20, 0),
                        "Aftenvagt"
                )
        );
    }
}
