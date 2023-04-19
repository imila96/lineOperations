package com.lineOperation.crud.exception;

public class ShiftUpdateException extends Exception {
    public ShiftUpdateException(String message) {
        super(message);
    }

    public ShiftUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
