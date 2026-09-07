package org.example.uberreviewservice.exception;

public class DriverHasActiveBookingsException extends RuntimeException {
    public DriverHasActiveBookingsException(Long driverId, long bookingCount) {
        super("Cannot delete driver with id: " + driverId +
                ". " + bookingCount + " existing booking(s) reference this driver.");
    }
}