package com.hireflow.backend.exception;

/** İş kuralı ihlali; ApiExceptionHandler bunu 400'e çevirir. */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
