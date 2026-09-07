package org.example.uberreviewservice.exception;

// Thrown when trying to review a booking that hasn't reached COMPLETED yet
public class InvalidBookingStateForReviewException extends RuntimeException {
    public InvalidBookingStateForReviewException(String currentStatus) {
        super("Cannot review a booking with status " + currentStatus
                + ". Only COMPLETED bookings can be reviewed.");
    }
}