/*package com.example.bolgebaderne.controller;

import com.example.bolgebaderne.dto.WaitlistEntryDTO;
import com.example.bolgebaderne.dto.WaitlistJoinRequest;
import com.example.bolgebaderne.dto.WaitlistStatusDTO;
import com.example.bolgebaderne.model.WaitlistEntry;
import com.example.bolgebaderne.service.WaitlistEntryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/timeslots")
public class WaitlistStatusController {

    private final WaitlistEntryService waitlistService;

    public WaitlistStatusController(WaitlistEntryService waitlistService) {
        this.waitlistService = waitlistService;
    }

    // GET /api/timeslots/1/waitlist?userId=2
    @GetMapping("/{timeslotId}/waitlist")
    public WaitlistStatusDTO status(
            @PathVariable int timeslotId,
            @RequestParam(required = false) Integer userId)
    {
        return waitlistService.getWaitlistStatus(timeslotId, userId);
    }

    // POST /api/timeslots/1/waitlist  body: {"userId":2,"type":"STANDARD"}
    @PostMapping("/{timeslotId}/waitlist")
    public ResponseEntity<WaitlistEntryDTO> join(
            @PathVariable int timeslotId,
            @RequestBody WaitlistJoinRequest request
    ) {
        WaitlistEntry entry = waitlistService.joinWaitlist(timeslotId, request.getUserId(), request.getType());

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
*/