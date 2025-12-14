package com.example.bolgebaderne.dto;

public class WaitlistStatusDTO {
    private int timeslotId;
    private boolean fullyBooked;
    private int waitlistCount;
    private Integer position; // null hvis userId ikke sendes
    private String message;

    public WaitlistStatusDTO(int timeslotId, boolean fullyBooked, int waitlistCount, Integer position, String message) {
        this.timeslotId = timeslotId;
        this.fullyBooked = fullyBooked;
        this.waitlistCount = waitlistCount;
        this.position = position;
        this.message = message;
    }

    public int getTimeslotId() { return timeslotId; }
    public boolean isFullyBooked() { return fullyBooked; }
    public int getWaitlistCount() { return waitlistCount; }
    public Integer getPosition() { return position; }
    public String getMessage() { return message; }
}
