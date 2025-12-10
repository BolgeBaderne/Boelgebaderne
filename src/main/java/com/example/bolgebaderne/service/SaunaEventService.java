package com.example.bolgebaderne.service;

import com.example.bolgebaderne.dto.SaunaEventAdminRequestDTO;
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

    public SaunaEvent createEvent(SaunaEventAdminRequestDTO dto) {
        SaunaEvent event = new SaunaEvent();
        event.setCurrentBookings(0);       // nye events starter uden bookinger
        copyDtoToEntity(dto, event);
        return repository.save(event);
    }

    // ===== Opdatering =====

    public SaunaEvent updateEvent(int id, SaunaEventAdminRequestDTO dto) {
        SaunaEvent event = getById(id);    // smider EventNotFoundException hvis ikke findes
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

    private void copyDtoToEntity(SaunaEventAdminRequestDTO dto, SaunaEvent event) {

        // title (default hvis null)
        if (dto.title() != null && !dto.title().isBlank()) {
            event.setTitle(dto.title());
        } else if (event.getTitle() == null) {
            event.setTitle("Saunagus");
        }

        event.setGusmesterName(dto.saunagusMasterName());
        event.setGusmesterImageUrl(dto.saunagusMasterImageUrl());
        event.setDescription(dto.description());
        event.setDurationMinutes(dto.durationMinutes());
        event.setCapacity(dto.capacity());
        event.setPrice(dto.price());

        // start_time: hvis du sender noget, brug det – ellers nu
        if (dto.start_time() != null) {
            event.setStartTime(LocalDateTime.of(
                    LocalDate.now(),
                    dto.start_time()
            ));
        } else if (event.getStartTime() == null) {
            event.setStartTime(LocalDateTime.now());
        }

        // status: brug værdi fra DTO hvis muligt, ellers UPCOMING
        EventStatus status = EventStatus.UPCOMING;   // default
        if (dto.status() != null && !dto.status().isBlank()) {
            status = EventStatus.valueOf(dto.status().toUpperCase());
        }
        event.setStatus(status);
    }
}
