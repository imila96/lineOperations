package com.lineOperation.crud.exception;

public class InvalidLineIdException extends RuntimeException {

    public InvalidLineIdException(String message) {
        super(message);
    }

    public InvalidLineIdException(String message, Throwable cause) {
        super(message, cause);
    }
}
