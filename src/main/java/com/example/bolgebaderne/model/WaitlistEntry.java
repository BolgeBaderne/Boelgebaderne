package com.example.bolgebaderne.model;

import java.time.LocalDateTime;

public class WaitlistEntry
{
    private int entryId;
    private int position;
    private LocalDateTime createdAt;
    private boolean promoted;
    private WaitlistType type;

    public void promoteToBooking() {
        this.promoted = true;
        // TODO: convert to booking logic
    }

    public WaitlistEntry(int entryId, int position, LocalDateTime createdAt, boolean promoted, WaitlistType type)
    {
        this.entryId = entryId;
        this.position = position;
        this.createdAt = createdAt;
        this.promoted = promoted;
        this.type = type;
    }

    public int getEntryId() {return entryId;}
    public int getPosition() {return position;}
    public LocalDateTime getCreatedAt() {return createdAt;}
    public boolean isPromoted() {return promoted;}
    public WaitlistType getType() {return type;}

    public void setEntryId(int entryId) {this.entryId = entryId;}
    public void setPosition(int position) {this.position = position;}
    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}
    public void setPromoted(boolean promoted) {this.promoted = promoted;}
    public void setType(WaitlistType type) {this.type = type;}
}
