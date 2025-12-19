package com.example.bolgebaderne.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(TimeSlotFullException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public Map<String, String> handleFull(TimeSlotFullException e) {
    return Map.of("error", e.getMessage());
  }

  @ExceptionHandler(NotMemberEligibleException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  @ResponseBody
  public Map<String, String> handleNotAllowed(NotMemberEligibleException e) {
    return Map.of("error", e.getMessage());
  }

  @ExceptionHandler(AlreadyBookedException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public Map<String, String> handleAlreadyBooked(AlreadyBookedException e) {
    return Map.of("error", e.getMessage());
  }

  @ExceptionHandler(EventNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)  // ✅ Returner 404
  @ResponseBody
  public Map<String, String> handleEventNotFound(EventNotFoundException e) {
    return Map.of(
            "errorType", "EventNotFoundException",
            "error", e.getMessage()
    );
  }

  @ExceptionHandler(Exception. class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public Map<String, String> handleGeneric(Exception e) {
    e.printStackTrace();
    return Map.of(
            "errorType", e.getClass().getSimpleName(),
            "error", e.getMessage() != null ? e.getMessage() : "Der skete en fejl.  Prøv igen."
    );
  }

}  // ✅ Luk klassen HER