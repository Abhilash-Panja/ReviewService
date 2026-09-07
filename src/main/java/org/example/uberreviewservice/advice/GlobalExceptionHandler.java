package org.example.uberreviewservice.advice;

import jakarta.servlet.http.HttpServletRequest;
import org.example.uberreviewservice.dto.error.ErrorResponseDTO;
import org.example.uberreviewservice.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleBookingNotFound(
            BookingNotFoundException ex, WebRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(PassengerNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handlePassengerNotFound(
            PassengerNotFoundException ex, WebRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidBookingStatusTransitionException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidTransition(
            InvalidBookingStatusTransitionException ex, WebRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(NoDriversAvailableException.class)
    public ResponseEntity<ErrorResponseDTO> handleNoDrivers(
            NoDriversAvailableException ex, WebRequest request) {
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage(), request);
    }

    // Catch-all safety net — never let a raw 500 with a stack trace leak to the client
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneric(
            Exception ex, WebRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong", request);
    }

    private ResponseEntity<ErrorResponseDTO> buildResponse(
            HttpStatus status, String message, WebRequest request) {
        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(error, status);
    }
    @ExceptionHandler(DriverNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleDriverNotFound(
            DriverNotFoundException ex, WebRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(DuplicateLicenceNumberException.class)
    public ResponseEntity<ErrorResponseDTO> handleDuplicateLicence(
            DuplicateLicenceNumberException ex, WebRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }
    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleReviewNotFound(
            ReviewNotFoundException ex, WebRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidBookingStateForReviewException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidBookingState(
            InvalidBookingStateForReviewException ex, WebRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(ReviewAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleReviewAlreadyExists(
            ReviewAlreadyExistsException ex, WebRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }
    @ExceptionHandler(PassengerHasActiveBookingsException.class)
    public ResponseEntity<ErrorResponseDTO> handlePassengerHasBookings(
            PassengerHasActiveBookingsException ex, HttpServletRequest request) {
        ErrorResponseDTO error = buildError(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");
        ErrorResponseDTO error = buildError(HttpStatus.BAD_REQUEST, message, request.getRequestURI());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(DriverHasActiveBookingsException.class)
    public ResponseEntity<ErrorResponseDTO> handleDriverHasBookings(
            DriverHasActiveBookingsException ex, HttpServletRequest request) {
        ErrorResponseDTO error = buildError(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }
    private ErrorResponseDTO buildError(HttpStatus status, String message, String path) {
        return ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .build();
    }
}