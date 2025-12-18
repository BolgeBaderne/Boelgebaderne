package com.example.bolgebaderne.exceptions;

public class WaitlistNotAllowedException extends RuntimeException {
    public WaitlistNotAllowedException(String message) {
        super(message);
    }
}
