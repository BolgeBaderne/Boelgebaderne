package com.example.bolgebaderne.service;

import com.example.bolgebaderne.exceptions.EventNotFoundException;
import com.example.bolgebaderne.exceptions.WaitlistNotAllowedException;
import com.example.bolgebaderne.model.*;
import com.example.bolgebaderne.repository.SaunaEventRepository;
import com.example.bolgebaderne.repository.UserRepository;
import com.example.bolgebaderne.repository.WaitlistEntryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WaitlistEntryService {
    private WaitlistEntryRepository waitlistRepo;
    private SaunaEventRepository eventRepo;
    private UserRepository userRepo;


    public WaitlistEntryService(WaitlistEntryRepository waitlistRepo,
                           SaunaEventRepository eventRepo,
                           UserRepository userRepo) {
        this.waitlistRepo = waitlistRepo;
        this.eventRepo = eventRepo;
        this.userRepo = userRepo;
    }

    public WaitlistEntry addToWaitlist(int userId, int eventId, WaitlistType type) {
        User user = userRepo.findById(userId).orElseThrow();
        SaunaEvent event = eventRepo.findById(eventId).orElseThrow();

        // metode til at undgå dubletter
        if (waitlistRepo.existsByUserAndEvent(user, event)) {
            throw new IllegalStateException("User is already on waitlist for this event");
        }

        // Find næste position i køen
        int count = waitlistRepo.countBySaunaEvent(event);
        int nextPosition = count + 1;

        WaitlistEntry entry = new WaitlistEntry();
        entry.setUser(user);
        entry.setEvent(event);
        entry.setType(type);
        entry.setPosition(nextPosition);
        entry.setCreatedAt(LocalDateTime.now());
        entry.setPromoted(false);

        return waitlistRepo.save(entry);
    }

    //Metode til at holde styr på ventelisten, og give listen en prioriteret rækkefølge
    public void promoteFirstInQueue(SaunaEvent saunaEvent) {
        List<WaitlistEntry> queue = waitlistRepo.findBySaunaEventOrderByPositionAsc(saunaEvent);

        if (queue.isEmpty()) return;

        WaitlistEntry next = queue.get(0); //Her så kører den listen igennem, og sørger for at den første i ventelisten
                                          //kommer først i listen
        next.promoteToBooking();
        waitlistRepo.delete(next); //her bliver 'brugeren' slettet efter de er blevet promoted væk fra ventelisten
    }


    public SaunaEvent getEventOrThrow(int eventId) {
        return eventRepo.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(
                        "Event not found with id: " + eventId
                ));
    }


    public int getWaitlistCount(SaunaEvent saunaEvent) {
        return waitlistRepo.countBySaunaEvent(saunaEvent);
    }


    public boolean isEventFullyBooked(SaunaEvent saunaEvent) {
        // Brug bare de felter der allerede ligger på eventet
        int current = saunaEvent.getCurrentBookings();
        int capacity = saunaEvent.getCapacity();
        return current >= capacity;
    }


    //Metode til at tilmelde bruger til en venteliste
    public WaitlistEntry joinWaitlist(int eventId, int userId, WaitlistType type) {

        SaunaEvent event = getEventOrThrow(eventId);

        // 1) Tjek om tiden er fuldt booket
        if (!isEventFullyBooked(event)) {
            throw new WaitlistNotAllowedException("Tiden er ikke fuldt booket – venteliste er ikke tilladt.");
        }

        // 2) Find user
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        // (Hvis I har en custom UserNotFoundException, brug den i stedet)

        // 3) Tjek om brugeren allerede står på listen
        boolean alreadyOnList = waitlistRepo.existsByUserAndEvent(user, event);
        if (alreadyOnList) {
            throw new WaitlistNotAllowedException("Brugeren står allerede på ventelisten.");
        }

        // 4) Beregn ny position (sidst i køen)
        int position = waitlistRepo.countByEvent(event) + 1;

        // 5) Opret entry
        WaitlistEntry entry = new WaitlistEntry();
        entry.setPosition(position);
        entry.setCreatedAt(LocalDateTime.now());
        entry.setPromoted(false);
        entry.setType(type);
        entry.setUser(user);
        entry.setEvent(event);

        // 6) Gem og returnér
        return waitlistRepo.save(entry);
    }
}
