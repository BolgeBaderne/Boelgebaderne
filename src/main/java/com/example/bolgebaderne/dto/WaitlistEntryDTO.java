package com.example.bolgebaderne.dto;

public class WaitlistEntryDTO {

    private int entryId;
    private int position;
    private int userId;
    private int eventId;
    private String type;

    public WaitlistEntryDTO(int entryId, int position, int userId, int eventId, String type) {
        this.entryId = entryId;
        this.position = position;
        this.userId = userId;
        this.eventId = eventId;
        this.type = type;
    }

    public int getEntryId() {
        return entryId;
    }

    public int getPosition() {
        return position;
    }

    public int getUserId() {
        return userId;
    }

    public int getEventId() {
        return eventId;
    }

    public String getType() {
        return type;
    }
}
