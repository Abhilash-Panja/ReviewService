package org.example.uberreviewservice.exception;

public class PassengerNotFoundException extends RuntimeException {
    public PassengerNotFoundException(Long id) {
        super("Passenger not found with id: " + id);
    }
}