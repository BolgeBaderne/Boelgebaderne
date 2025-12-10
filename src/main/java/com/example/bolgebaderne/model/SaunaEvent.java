package com.example.bolgebaderne.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sauna_events")
public class SaunaEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private int eventId;

    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @Column(name = "gusmester_name", nullable = false, length = 100)
    private String gusmesterName;
    private String gusmesterImageUrl;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "duration_minutes", nullable = false)
    private int durationMinutes;

    @Column(name = "capacity", nullable = false)
    private int capacity;

    @Column(name = "price", nullable = false)
    private double price;

    private int currentBookings;
    private int availableSpots;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private EventStatus status;

    public SaunaEvent() {}

    // Din egen constructor
    public SaunaEvent(int eventId, String title, String description, String gusmesterName, String gusmesterImageUrl,
                      LocalDateTime startTime, int durationMinutes, int capacity,
                      double price, EventStatus status, int currentBookings, int availableSpots) {
        this.eventId = eventId;
        this.title = title;
        this.description = description;
        this.gusmesterName = gusmesterName;
        this.gusmesterImageUrl = gusmesterImageUrl;
        this.startTime = startTime;
        this.durationMinutes = durationMinutes;
        this.capacity = capacity;
        this.price = price;
        this.status = status;
        this.currentBookings = currentBookings;
        this.availableSpots = availableSpots;
    }


    //Getters and Setters
    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGusmesterName() {
        return gusmesterName;
    }

    public void setGusmesterName(String gusmesterName) {
        this.gusmesterName = gusmesterName;
    }

    public String getGusmesterImageUrl() {
        return gusmesterImageUrl;
    }

    public void setgusmesterImageUrl(String gusmesterImageUrl) {
        this.gusmesterImageUrl = gusmesterImageUrl;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public int getCurrentBookings() {
        return currentBookings;
    }

    public void setCurrentBooking(int currentBookings) {
        this.currentBookings = currentBookings;
    }

    public int getAvailableSpots() {
        return availableSpots;
    }

    public void setAvailableSpots(int availableSpots) {
        this.availableSpots = availableSpots;
    }


}
