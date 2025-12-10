package com.example.bolgebaderne.controller;

import com.example.bolgebaderne.dto.SaunaAdminEventDTO;
import com.example.bolgebaderne.dto.SaunaEventDTO;
import com.example.bolgebaderne.model.SaunaEvent;
import com.example.bolgebaderne.service.SaunaEventService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/events")
@PreAuthorize("hasRole('ADMIN')")
public class SaunaAdminEventController {

    private final SaunaEventService saunaEventService;

    public SaunaAdminEventController(SaunaEventService saunaEventService) {
        this.saunaEventService = saunaEventService;
    }

    @GetMapping
    public List<SaunaEventDTO> getAllForAdmin() {
        return saunaEventService.getAllEvents()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public SaunaEventDTO getEvent(@PathVariable int id) {
        return toDTO(saunaEventService.getById(id));
    }

    @PostMapping
    public SaunaEventDTO create(@RequestBody SaunaAdminEventDTO dto) {
        SaunaEvent created = saunaEventService.createEvent(dto);
        return toDTO(created);
    }

    @PutMapping("/{id}")
    public SaunaEventDTO update(@PathVariable int id, @RequestBody SaunaAdminEventDTO dto) {
        SaunaEvent updated = saunaEventService.updateEvent(id, dto);
        return toDTO(updated);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        saunaEventService.deleteEvent(id);
    }

    private SaunaEventDTO toDTO(SaunaEvent e) {
        return new SaunaEventDTO(
                e.getEventId(),
                e.getTitle(),
                e.getGusmesterName(),
                e.getGusmesterImageUrl(),
                e.getDescription(),
                e.getStartTime(),
                e.getDurationMinutes(),
                e.getCapacity(),
                e.getPrice(),
                e.getCurrentBookings(),
                e.getAvailableSpots(),
                e.getStatus().name()
        );
    }
}
