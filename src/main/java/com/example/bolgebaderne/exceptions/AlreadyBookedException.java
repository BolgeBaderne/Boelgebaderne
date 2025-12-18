package com.example.bolgebaderne.exceptions;

public class AlreadyBookedException extends RuntimeException {
  public AlreadyBookedException(String message) {
    super(message);
  }
}
