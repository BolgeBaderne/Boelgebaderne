package com.example.bolgebaderne.service;

import com.example.bolgebaderne.controller.EventAdminRequest;
import com.example.bolgebaderne.exceptions.EventNotFoundException;
import com.example.bolgebaderne.model.SaunaEvent;
import com.example.bolgebaderne.repository.SaunaEventRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SaunaEventService {

    private final SaunaEventRepository repository;

    public SaunaEventService(SaunaEventRepository repository) {
        this.repository = repository;
    }

    // ===== Læsning =====

    public List<SaunaEvent> getAllEvents() {
        return repository.findAll();
    }

    public SaunaEvent getById(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Det valgte event findes ikke."));
    }

    // ===== Oprettelse =====

    public SaunaEvent createEvent(EventAdminRequest dto) {
        SaunaEvent event = new SaunaEvent();
        copyDtoToEntity(dto, event);
        return repository.save(event);
    }

    // ===== Opdatering =====

    public SaunaEvent updateEvent(int id, EventAdminRequest dto) {
        SaunaEvent event = getById(id);   // smider EventNotFoundException hvis ikke findes
        copyDtoToEntity(dto, event);
        return repository.save(event);
    }

    // ===== Sletning =====

    public void deleteEvent(int id) {
        if (!repository.existsById(id)) {
            throw new EventNotFoundException("Det valgte event findes ikke.");
        }
        repository.deleteById(id);
    }

    // ===== Helper: kopier data fra admin-DTO til entity =====

    private void copyDtoToEntity(EventAdminRequest dto, SaunaEvent event) {
        event.setGusmesterName(dto.getSaunagusMasterName());
        event.setGusmesterImageUrl(dto.getSaunagusMasterImageUrl());
        event.setDescription(dto.getDescription());
        event.setDurationMinutes(dto.getDurationMinutes());
        event.setCapacity(dto.getCapacity());
        event.setPrice(dto.getPrice());
    }
}
