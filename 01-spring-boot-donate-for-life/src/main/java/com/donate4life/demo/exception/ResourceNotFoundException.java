package com.donate4life.demo.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message); // this makes getMessage() work
    }
}
