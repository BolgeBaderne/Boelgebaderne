package com.example.bolgebaderne.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Integer bookingId;

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
    private SaunaEvent saunaEvent;

    public Booking() {
    }

    public Booking(int bookingId, LocalDateTime createdAt, BookingStatus status,
                   User user, SaunaEvent saunaEvent) {
        this.bookingId = bookingId;
        this.createdAt = createdAt;
        this.status = bookingStatus;
        this.user = user;
        this.saunaEvent = saunaEvent;
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

    public SaunaEvent getSaunaEvent() { return saunaEvent; }
    public void setSaunaEvent(SaunaEvent saunaEvent) { this.saunaEvent = saunaEvent; }
}
