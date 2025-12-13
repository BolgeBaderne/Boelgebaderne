package com.example.bolgebaderne.service;

import com.example.bolgebaderne.dto.SaunaAdminEventDTO;
import com.example.bolgebaderne.dto.SaunaAdminEventDTO;
import com.example.bolgebaderne.exceptions.EventNotFoundException;
import com.example.bolgebaderne.model.EventStatus;
import com.example.bolgebaderne.model.SaunaEvent;
import com.example.bolgebaderne.repository.SaunaEventRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    public SaunaEvent createEvent(SaunaAdminEventDTO dto) {
        SaunaEvent event = new SaunaEvent();
        copyDtoToEntity(dto, event);

        // Når man opretter et event → 0 bookinger fra start
        event.setCurrentBookings(0);

        return repository.save(event);
    }

    // ===== Opdatering =====

    public SaunaEvent updateEvent(int id, SaunaAdminEventDTO dto) {
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

    private void copyDtoToEntity(SaunaAdminEventDTO dto, SaunaEvent event) {
        event.setTitle(dto.title());
        event.setGusmesterName(dto.saunagusMasterName());
        event.setGusmesterImageUrl(dto.saunagusMasterImageUrl());
        event.setDescription(dto.description());
        event.setDurationMinutes(dto.durationMinutes());
        event.setCapacity(dto.capacity());
        event.setPrice(dto.price());

        // Konvertér LocalTime (kun klokkeslæt) → LocalDateTime (i dag + klokkeslæt)
        if (dto.start_time() != null) {
            LocalDateTime start = LocalDateTime.of(LocalDate.now(), dto.start_time());
            event.setStartTime(start);
        }

        // Konvertér status-string → enum (UPPERCASE)
        if (dto.status() != null) {
            event.setStatus(EventStatus.valueOf(dto.status().toUpperCase()));
        }
    }
}
