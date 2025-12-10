package com.example.bolgebaderne.service;

import com.example.bolgebaderne.exceptions.EventNotFoundException;
import com.example.bolgebaderne.model.SaunaEvent;
import com.example.bolgebaderne.model.User;
import com.example.bolgebaderne.model.WaitlistEntry;
import com.example.bolgebaderne.model.WaitlistType;
import com.example.bolgebaderne.repository.BookingRepository;
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
    private BookingRepository bookingRepo;

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
        if (waitlistRepo.existByUserAndEvent(user, event)) {
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
        // Hvor mange bookings findes der for det her event?
        int bookingCount = bookingRepo.countBySaunaEvent(saunaEvent);

        // Regner ud hvor mange pladser vi har i alt.
        int capacity = saunaEvent.getCapacity();

        // Fuldt booket hvis antal bookings >= kapacitet
        return bookingCount >= capacity;
    }
}
