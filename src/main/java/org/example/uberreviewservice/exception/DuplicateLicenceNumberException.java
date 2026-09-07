package org.example.uberreviewservice.exception;

// Thrown when a licence number is reused across drivers — should be unique in real life
public class DuplicateLicenceNumberException extends RuntimeException {
    public DuplicateLicenceNumberException(String licenceNumber) {
        super("A driver with licence number '" + licenceNumber + "' already exists");
    }
}