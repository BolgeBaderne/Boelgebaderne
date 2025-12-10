package com.example.bolgebaderne.controller;

import com.example.bolgebaderne.dto.WaitlistEntryDTO;
import com.example.bolgebaderne.model.SaunaEvent;
import com.example.bolgebaderne.service.WaitlistEntryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/timeslots")
public class WaitlistStatusController {
    private WaitlistEntryService waitlistService;

    public WaitlistStatusController(WaitlistEntryService waitlistService) {
        this.waitlistService = waitlistService;
    }

    @GetMapping("/{id}/waitlist")
    public ResponseEntity<WaitlistEntryDTO> getWaitlistStatus(@PathVariable int id) {

        SaunaEvent event = waitlistService.getEventOrThrow(id);

        boolean fullyBooked = waitlistService.isEventFullyBooked(event);
        int waitlistCount = waitlistService.getWaitlistCount(event);

        WaitlistEntryDTO dto = new WaitlistEntryDTO(fullyBooked, waitlistCount);

        return ResponseEntity.ok(dto);
    }
}
