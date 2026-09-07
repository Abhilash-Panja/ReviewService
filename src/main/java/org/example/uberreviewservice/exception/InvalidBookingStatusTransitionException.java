package org.example.uberreviewservice.exception;

// Thrown when a requested status change isn't legal from the current state
// e.g. trying to go COMPLETED -> ASSIGNED_DRIVER
public class InvalidBookingStatusTransitionException extends RuntimeException {
    public InvalidBookingStatusTransitionException(String currentStatus, String newStatus) {
        super("Cannot transition booking from " + currentStatus + " to " + newStatus);
    }
}