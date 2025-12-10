package com.example.bolgebaderne.controller;

import com.example.bolgebaderne.dto.SaunaEventDTO;
import com.example.bolgebaderne.model.SaunaEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.bolgebaderne.service.SaunaEventService;

import java.util.List;
@RestController
@RequestMapping("/api/events")
public class SaunaEventController {

    private final SaunaEventService saunaEventService;

    public SaunaEventController(SaunaEventService saunaEventService) {
        this.saunaEventService = saunaEventService;
    }

    @GetMapping
    public List<SaunaEventDTO> getAllEvents() {
        return saunaEventService.getAllEvents()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public SaunaEventDTO getEventById(@PathVariable int id){
        SaunaEvent event = saunaEventService.getById(id);
        return toDTO(event);
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
