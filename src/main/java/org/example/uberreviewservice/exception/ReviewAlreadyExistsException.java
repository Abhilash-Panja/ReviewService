package org.example.uberreviewservice.exception;

// Thrown when a booking already has a review — enforces "one review per booking"
// at the application level too, backing up the DB's unique constraint
public class ReviewAlreadyExistsException extends RuntimeException {
    public ReviewAlreadyExistsException(Long bookingId) {
        super("A review already exists for booking id: " + bookingId);
    }
}