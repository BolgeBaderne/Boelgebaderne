package com.example.bolgebaderne.exceptions;

import com.example.bolgebaderne.dto.ApiErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalHandleException {

    // 404 – når et event ikke findes
    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleEventNotFound(
            EventNotFoundException ex,
            HttpServletRequest request
    ) {
        ApiErrorResponseDTO body = new ApiErrorResponseDTO(
                404,
                "Not Found",
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    // 400 – fx hvis status er forkert værdi eller andet "dårligt input"
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {
        ApiErrorResponseDTO body = new ApiErrorResponseDTO(
                400,
                "Bad Request",
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // 500 – fallback for alle andre fejl
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponseDTO> handleGeneric(
            Exception ex,
            HttpServletRequest request
    ) {
        ApiErrorResponseDTO body = new ApiErrorResponseDTO(
                500,
                "Internal Server Error",
                "Der skete en uventet fejl. Prøv igen senere.",
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
