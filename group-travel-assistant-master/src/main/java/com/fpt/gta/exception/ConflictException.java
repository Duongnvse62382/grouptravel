package com.fpt.gta.exception;

public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }

    public ConflictException() {
    }

    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
