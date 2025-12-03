package com.example.bolgebaderne.service;

import com.example.bolgebaderne.model.EventStatus;
import com.example.bolgebaderne.model.SaunaEvent;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Locale.filter;

@Service
public class SaunaEventService {
    private final List<SaunaEvent> events = new ArrayList<>();

    public SaunaEventService() {
        // Disse eksempler er MOCK DATA (så /events virker direkte)
        events.add(new SaunaEvent(
                1,
                "Morgen Saunagus",
                "En blid start på dagen med æteriske olier.",
                "Anne Larsen",
                "https://example.com/master1.jpg",
                LocalDateTime.now().plusDays(1),
                45,
                12,
                120.0,
                5,
                EventStatus.UPCOMING
        ));

        events.add(new SaunaEvent(
                2,
                "Aften Saunagus",
                "Intens saunagus med fokus på afslapning.",
                "Jonas Petersen",
                "https://example.com/master2.jpg",
                LocalDateTime.now().plusDays(2),
                60,
                14,
                150.0,
                10,
                EventStatus.UPCOMING
        ));
    }

    //Hent alle events
    public List<SaunaEvent> getAllEvents(){
        return events;
    }

    //Hent Event efter id
    public SaunaEvent getById(int id)
    {
        return events.stream()
                .filter(e -> e.getEventId() == id)
                .findFirst()
                .orElse(null);
    }

}


