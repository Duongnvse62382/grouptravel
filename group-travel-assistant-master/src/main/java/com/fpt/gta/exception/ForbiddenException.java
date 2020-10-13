package com.fpt.gta.exception;

public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException() {
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }
}
