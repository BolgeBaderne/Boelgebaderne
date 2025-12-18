package com.example.bolgebaderne.service;

import com.example.bolgebaderne.dto.WaitlistStatusDTO;
import com.example.bolgebaderne.exceptions.EventNotFoundException;
import com.example.bolgebaderne.exceptions.WaitlistNotAllowedException;
import com.example.bolgebaderne.model.*;
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
    private final BookingRepository bookingRepo;
    private final NotificationService notificationService;

    public WaitlistEntryService(WaitlistEntryRepository waitlistRepo,
                                SaunaEventRepository eventRepo, UserRepository userRepo,
                                BookingRepository bookingRepo,
                                NotificationService notificationService) {
        this.waitlistRepo = waitlistRepo;
        this.eventRepo = eventRepo;
        this.userRepo = userRepo;
        this.bookingRepo = bookingRepo;
        this.notificationService = notificationService;
    }

    public WaitlistEntry addToWaitlist(int userId, int eventId, WaitlistType type) {
        User user = userRepo.findById(userId).orElseThrow();
        SaunaEvent event = eventRepo.findById(eventId).orElseThrow();

        // metode til at undgå dubletter
        if (waitlistRepo.existsByUserAndSaunaEvent(user, event)) {
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
        List<WaitlistEntry> queue =
                waitlistRepo.findBySaunaEventOrderByPositionAsc(saunaEvent);

        if (queue.isEmpty()) {
            return; // ingen på ventelisten
        }

        WaitlistEntry next = queue.get(0);   // første i køen

        // 1) Lav booking til brugeren
        Booking booking = new Booking();
        booking.setUser(next.getUser());
        booking.setSaunaEvent(saunaEvent);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setStatus(BookingStatus.ACTIVE);

        bookingRepo.save(booking);

        // 2) Fjern fra ventelisten
        waitlistRepo.delete(next);

        // 3) Send "notifikation"
        notificationService.sendWaitlistPromotion(next.getUser(), saunaEvent);
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
    public WaitlistStatusDTO getWaitlistStatus(int eventId, Integer userId) {
        SaunaEvent event = getEventOrThrow(eventId);

        List<WaitlistEntry> entries = waitlistRepo.findBySaunaEventOrderByPositionAsc(event);

        Integer userPosition = null;
        if (userId != null) {
            for (WaitlistEntry entry : entries) {
                if (entry.getUser().getUserId() == userId) {
                    userPosition = entry.getPosition();
                    break;
                }
            }
        }

        boolean fullyBooked = isEventFullyBooked(event);
        String message = fullyBooked ? "Tidspunkt er fuldt booket" : "Ledige pladser tilgængelige";

        return new WaitlistStatusDTO(
                eventId,
                fullyBooked,
                entries.size(),
                userPosition,
                message
        );
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
        boolean alreadyOnList = waitlistRepo.existsByUserAndSaunaEvent(user, event);
        if (alreadyOnList) {
            throw new WaitlistNotAllowedException("Brugeren står allerede på ventelisten.");
        }

        // 4) Beregn ny position (sidst i køen)
        int position = waitlistRepo.countBySaunaEvent(event) + 1;

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
