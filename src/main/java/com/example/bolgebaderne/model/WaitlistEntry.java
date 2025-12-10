package com.example.bolgebaderne.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "waitlist_entries")
public class WaitlistEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "entry_id")
    private int entryId;

    @Column(name = "position", nullable = false)
    private int position;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "promoted", nullable = false)
    private boolean promoted;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private WaitlistType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private SaunaEvent event;

    public WaitlistEntry() {
    }

    public WaitlistEntry(int entryId, int position, LocalDateTime createdAt,
                         boolean promoted, WaitlistType type,
                         User user, SaunaEvent event) {
        this.entryId = entryId;
        this.position = position;
        this.createdAt = createdAt;
        this.promoted = promoted;
        this.type = type;
        this.user = user;
        this.event = event;
    }

    public void promoteToBooking() {
        this.promoted = true;
        // TODO: lave logik der opretter en Booking baseret på denne
    }

    public int getEntryId() {
        return entryId;
    }

    public void setEntryId(int entryId) {
        this.entryId = entryId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isPromoted() {
        return promoted;
    }

    public void setPromoted(boolean promoted) {
        this.promoted = promoted;
    }

    public WaitlistType getType() {
        return type;
    }

    public void setType(WaitlistType type) {
        this.type = type;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public SaunaEvent getEvent() {
        return event;
    }

    public void setEvent(SaunaEvent event) {
        this.event = event;
    }
}
