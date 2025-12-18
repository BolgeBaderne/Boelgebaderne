package com.example.bolgebaderne.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sauna_events")
public class SaunaEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int eventId;

    private String title;
    private String description;
    private String gusmesterName;
    private String gusmesterImageUrl;

    private LocalDateTime startTime;
    private int durationMinutes;
    private int capacity;
    private double price;

    private int currentBookings;

    @Enumerated(EnumType.STRING)
    private EventStatus status;

    public SaunaEvent() {}

    // Din egen constructor
    public SaunaEvent(int eventId, String title, String description, String gusmesterName,
                      String gusmesterImageUrl, LocalDateTime startTime, int durationMinutes,
                      int capacity, double price, int currentBookings, EventStatus status) {

        this.eventId = eventId;
        this.title = title;
        this.description = description;
        this.gusmesterName = gusmesterName;
        this.gusmesterImageUrl = gusmesterImageUrl;
        this.startTime = startTime;
        this.durationMinutes = durationMinutes;
        this.capacity = capacity;
        this.price = price;
        this.currentBookings = currentBookings;
        this.status = status;
    }



    // public SaunaEvent(int i, String title, String s, String s1, LocalDateTime start, int i1, int capacity, double price, EventStatus eventStatus) {
   // }

    public int getAvailableSpots() {
        return capacity - currentBookings;
    }

    // Getters + setters …

    public int getEventId() { return eventId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getGusmesterName() { return gusmesterName; }
    public String getGusmesterImageUrl() { return gusmesterImageUrl; }
    public LocalDateTime getStartTime() { return startTime; }
    public int getDurationMinutes() { return durationMinutes; }
    public int getCapacity() { return capacity; }
    public double getPrice() { return price; }
    public int getCurrentBookings() { return currentBookings; }
    public EventStatus getStatus() { return status; }

    public void setEventId(int eventId) { this.eventId = eventId; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setGusmesterName(String gusmesterName) { this.gusmesterName = gusmesterName; }
    public void setGusmesterImageUrl(String gusmesterImageUrl) { this.gusmesterImageUrl = gusmesterImageUrl; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public void setPrice(double price) { this.price = price; }
    public void setCurrentBookings(int currentBookings) { this.currentBookings = currentBookings; }
    public void setStatus(EventStatus status) { this.status = status; }
}