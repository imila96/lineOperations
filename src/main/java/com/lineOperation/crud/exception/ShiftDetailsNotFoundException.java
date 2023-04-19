package com.lineOperation.crud.exception;

import org.springframework.dao.DataAccessException;

public class ShiftDetailsNotFoundException extends RuntimeException {

    public ShiftDetailsNotFoundException(String message) {
        super(message);
    }

    public ShiftDetailsNotFoundException(Throwable cause) {
        super(cause);
    }

    public ShiftDetailsNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShiftDetailsNotFoundException(DataAccessException e) {
    }
}

