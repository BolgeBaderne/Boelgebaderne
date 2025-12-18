package com.example.bolgebaderne.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
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

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public Map<String, String> handleGeneric(Exception e) {
    e.printStackTrace(); // logger i konsollen

    return Map.of(
            "errorType", e.getClass().getSimpleName(),
            "error", e.getMessage() != null ? e.getMessage() : "Der skete en fejl. Prøv igen."
    );
  }
  @ExceptionHandler(AlreadyBookedException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public Map<String, String> handleAlreadyBooked(AlreadyBookedException e) {
    return Map.of("error", e.getMessage());
  }

}
@ExceptionHandler(EventNotFoundException.class)
public ResponseEntity<ErrorResponse> handleEventNotFoundException(EventNotFoundException ex) {
  ErrorResponse error = new ErrorResponse("EventNotFoundException", ex.getMessage());
  return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);  // 404 ikke 500
}
