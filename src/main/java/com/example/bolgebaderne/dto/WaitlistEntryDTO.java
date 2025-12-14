package com.example.bolgebaderne.dto;

public class WaitlistEntryDTO {
    private int entryId;
    private int position;
    private int userId;
    private int timeslotId;
    private String type;

    public WaitlistEntryDTO(int entryId, int position, int userId, int timeslotId, String type) {
        this.entryId = entryId;
        this.position = position;
        this.userId = userId;
        this.timeslotId = timeslotId;
        this.type = type;
    }

    public int getEntryId() { return entryId; }
    public int getPosition() { return position; }
    public int getUserId() { return userId; }
    public int getTimeslotId() { return timeslotId; }
    public String getType() { return type; }
}
