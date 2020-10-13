package com.fpt.gta.exception;

public class UnprocessableEntityException extends RuntimeException{

    public UnprocessableEntityException(String message) {
        super(message);
    }

    public UnprocessableEntityException(Throwable cause) {
        super(cause);
    }

    public UnprocessableEntityException() {
    }

    public UnprocessableEntityException(String message, Throwable cause) {
        super(message, cause);
    }
}
