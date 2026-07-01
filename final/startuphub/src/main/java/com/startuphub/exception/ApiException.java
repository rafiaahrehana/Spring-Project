package com.startuphub.exception;

import org.springframework.http.HttpStatus;

/**
 * Base exception for all application-level errors.
 * GlobalExceptionHandler converts these into structured ErrorResponse JSON.
 */
public class ApiException extends RuntimeException {

    private final HttpStatus status;

    public ApiException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
