package com.example.bolgebaderne.service;

import com.example.bolgebaderne.dto.ShiftResponseDTO;
import com.example.bolgebaderne.model.Shift;
import com.example.bolgebaderne.repository.ShiftRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ShiftService {

    private final ShiftRepository shiftRepository;

    public ShiftService(ShiftRepository shiftRepository) {
        this.shiftRepository = shiftRepository;
    }

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

    public List<ShiftResponseDTO> getShiftsForUser(int userId) {

        List<Shift> shifts = shiftRepository.findByUser_UserId(userId);
        List<ShiftResponseDTO> responses = new ArrayList<>();

        for (Shift shift : shifts) {
            responses.add(toShiftResponseDTO(shift));
        }

        return responses;
    }

    private ShiftResponseDTO toShiftResponseDTO(Shift shift) {
        return new ShiftResponseDTO(
                shift.getDate(),
                shift.getStartTime(),
                shift.getEndTime(),
                shift.getLabel()
        );
    }

}
