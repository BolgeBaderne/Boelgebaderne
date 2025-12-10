package com.example.bolgebaderne.exceptions;

public class InvalidSaunaEventException extends RuntimeException {
    public InvalidSaunaEventException(String message) {
        super(message);
    }
}