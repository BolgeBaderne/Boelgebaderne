package com.example.bolgebaderne.service;

import com.example.bolgebaderne.dto.SaunaEventAdminRequestDTO;
import com.example.bolgebaderne.exceptions.EventNotFoundException;
import com.example.bolgebaderne.exceptions.InvalidSaunaEventException;
import com.example.bolgebaderne.exceptions.SaunaEventHasBookingsException;
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
        validateDto(dto);

        SaunaEvent event = new SaunaEvent();
        copyDtoToEntity(dto, event);

        // nyt event → ingen bookinger endnu
        event.setCurrentBookings(0);

        return repository.save(event);
    }

    // ===== Opdatering =====

    public SaunaEvent updateEvent(int id, SaunaEventAdminRequestDTO dto) {
        validateDto(dto);

        SaunaEvent event = getById(id);   // smider EventNotFoundException hvis ikke findes
        copyDtoToEntity(dto, event);

        // currentBookings bevares, vi overskriver det ikke
        return repository.save(event);
    }

    // ===== Sletning =====

    public void deleteEvent(int id) {
        SaunaEvent event = getById(id);   // smider EventNotFoundException hvis ikke findes

        // Simpel “afhængigheds-check”: har eventet bookinger?
        if (event.getCurrentBookings() > 0) {
            throw new SaunaEventHasBookingsException(
                    "Event med id " + id + " har aktive bookinger og kan ikke slettes."
            );
        }

        repository.deleteById(id);
    }

    // ===== Helper: valider input =====

    private void validateDto(SaunaEventAdminRequestDTO dto) {
        if (dto.title() == null || dto.title().isBlank()) {
            throw new InvalidSaunaEventException("Titel må ikke være tom.");
        }
        if (dto.saunagusMasterName() == null || dto.saunagusMasterName().isBlank()) {
            throw new InvalidSaunaEventException("Gusmester-navn må ikke være tomt.");
        }
        if (dto.durationMinutes() <= 0) {
            throw new InvalidSaunaEventException("Varighed skal være større end 0.");
        }
        if (dto.capacity() <= 0) {
            throw new InvalidSaunaEventException("Kapacitet skal være større end 0.");
        }
        if (dto.price() < 0) {
            throw new InvalidSaunaEventException("Pris kan ikke være negativ.");
        }
    }

    // ===== Helper: kopier data fra admin-DTO til entity =====

    private void copyDtoToEntity(SaunaEventAdminRequestDTO dto, SaunaEvent event) {
        event.setTitle(dto.title());
        event.setGusmesterName(dto.saunagusMasterName());
        event.setGusmesterImageUrl(dto.saunagusMasterImageUrl());
        event.setDescription(dto.description());
        event.setDurationMinutes(dto.durationMinutes());
        event.setCapacity(dto.capacity());
        event.setPrice(dto.price());

        // start_time er kun LocalTime → vi sætter dato = i dag (simpel løsning)
        if (dto.start_time() != null) {
            LocalDateTime startDateTime = LocalDateTime.of(LocalDate.now(), dto.start_time());
            event.setStartTime(startDateTime);
        }

        // status: hvis null → default UPCOMING
        EventStatus status =
                (dto.status() == null || dto.status().isBlank())
                        ? EventStatus.UPCOMING
                        : EventStatus.valueOf(dto.status().toUpperCase());
        event.setStatus(status);
    }
}
