package org.example.uberreviewservice.exception;

// Thrown when the matching service can't find any driver at creation time
public class NoDriversAvailableException extends RuntimeException {
    public NoDriversAvailableException() {
        super("No drivers available at the moment. Please try again shortly.");
    }
}