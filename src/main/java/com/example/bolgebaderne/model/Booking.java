package com.example.bolgebaderne.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

public class Booking
{
    private int bookingId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private BookingStatus status;

    // FK til user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // fk_user_id i ER-modellen
    private User user;

    // FK til event
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private SaunaEvent event;

    public Booking() {
    }

    public Booking(int bookingId, LocalDateTime createdAt, BookingStatus status,
                   User user, SaunaEvent event) {
        this.bookingId = bookingId;
        this.createdAt = createdAt;
        this.status = status;
        this.user = user;
        this.event = event;
    }

    public void cancel() {
        this.status = BookingStatus.CANCELLED;
    }

    public boolean isActive() {
        return this.status == BookingStatus.ACTIVE;
    }

    public Booking(int bookingId, LocalDateTime createdAt, BookingStatus status)
    {
        this.bookingId = bookingId;
        this.createdAt = createdAt;
        this.status = status;
    }


    // getters & setters
    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public SaunaEvent getEvent() { return event; }
    public void setEvent(SaunaEvent event) { this.event = event; }
}
