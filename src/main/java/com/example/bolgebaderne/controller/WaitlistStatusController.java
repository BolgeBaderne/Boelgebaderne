package com.example.bolgebaderne.controller;

import com.example.bolgebaderne.dto.WaitlistEntryDTO;
import com.example.bolgebaderne.dto.WaitlistJoinRequest;
import com.example.bolgebaderne.dto.WaitlistStatusDTO;
import com.example.bolgebaderne.model.SaunaEvent;
import com.example.bolgebaderne.model.WaitlistEntry;
import com.example.bolgebaderne.service.WaitlistEntryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/timeslots")
public class WaitlistStatusController {
    private WaitlistEntryService waitlistService;

    public WaitlistStatusController(WaitlistEntryService waitlistService) {
        this.waitlistService = waitlistService;
    }

    @GetMapping("/{id}/waitlist")
    public ResponseEntity<WaitlistStatusDTO> getWaitlistStatus(@PathVariable int id) {

        SaunaEvent event = waitlistService.getEventOrThrow(id);

        boolean fullyBooked = waitlistService.isEventFullyBooked(event);
        int waitlistCount = waitlistService.getWaitlistCount(event);

        WaitlistStatusDTO dto = new WaitlistStatusDTO(fullyBooked, waitlistCount);

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{id}/waitlist")
    public ResponseEntity<WaitlistEntryDTO> joinWaitList(
            @PathVariable int id,
            @RequestBody WaitlistJoinRequest request) {

        WaitlistEntry entry = waitlistService.joinWaitlist(
                id,
                request.getUserId(),
                request.getType()
        );

        WaitlistEntryDTO dto = new WaitlistEntryDTO(
                entry.getEntryId(),
                entry.getPosition(),
                entry.getUser().getUserId(),
                entry.getSaunaEvent().getEventId(),
                entry.getType().name()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }
}
