package com.example.bolgebaderne.controller;

import com.example.bolgebaderne.dto.ShiftResponseDTO;
import com.example.bolgebaderne.model.User;
import com.example.bolgebaderne.service.ShiftService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/shifts")
public class ShiftController {

    private final ShiftService shiftService;

    public ShiftController(ShiftService shiftService) {
        this.shiftService = shiftService;
    }

    @GetMapping("/me")
    public List<ShiftResponseDTO> getMyShifts(
            @AuthenticationPrincipal User user
    ) {
        return shiftService.getShiftsForUser(user.getUserId());
    }
}
