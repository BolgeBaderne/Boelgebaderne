package com.example.bolgebaderne.exceptions;

public class TimeSlotFullException extends RuntimeException {
    public TimeSlotFullException(String message) {
        super(message);
    }
}
