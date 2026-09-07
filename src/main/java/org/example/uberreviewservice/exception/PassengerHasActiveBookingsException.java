package org.example.uberreviewservice.exception;

public class PassengerHasActiveBookingsException extends RuntimeException {
    public PassengerHasActiveBookingsException(Long passengerId, long bookingCount) {
        super("Cannot delete passenger with id: " + passengerId +
                ". " + bookingCount + " existing booking(s) reference this passenger.");
    }
}