package com.example.bolgebaderne.exceptions;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(String message){
        super(message); //Kald constructoren i parent-klassen, som er RuntimeException.
    }
}
