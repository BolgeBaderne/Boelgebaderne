package com.example.bolgebaderne.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sauna_event")
public class SaunaEvent {

    //Denne liste gør det nemt at hente et event inkl. ventelisten
    @OneToMany(mappedBy = "saunaEvent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WaitlistEntry> waitlistEntries = new ArrayList<>();

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
    private int availableSpots;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private EventStatus eventStatus;



    public SaunaEvent() {}

    // Din egen constructor
    public SaunaEvent(int eventId, String title, String description, String gusmesterName, String gusmesterImageUrl,
                      LocalDateTime startTime, int durationMinutes, int capacity,
                      double price, EventStatus eventStatus, int currentBookings, int availableSpots) {


        this.eventId = eventId;
        this.title = title;
        this.description = description;
        this.gusmesterName = gusmesterName;
        this.gusmesterImageUrl = gusmesterImageUrl;
        this.startTime = startTime;
        this.durationMinutes = durationMinutes;
        this.capacity = capacity;
        this.price = price;
        this.eventStatus = eventStatus;
        this.currentBookings = currentBookings;
        this.availableSpots = availableSpots;
    }

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

    public void setGusmesterImageUrl(String gusmesterImageUrl) {
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

    public int getCurrentBookings() {
        return currentBookings;
    }

    public void setCurrentBookings(int currentBookings) {
        this.currentBookings = currentBookings;
        this.availableSpots = this.capacity - currentBookings;

    }

    public int getAvailableSpots() {
            return capacity - currentBookings;

    }

    public void setAvailableSpots(int availableSpots) {
        this.availableSpots = availableSpots;
    }

    public EventStatus getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(EventStatus eventStatus) {
        this.eventStatus = eventStatus;
    }    }

