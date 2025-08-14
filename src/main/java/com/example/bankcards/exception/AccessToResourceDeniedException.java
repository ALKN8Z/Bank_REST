package com.example.bankcards.exception;

public class AccessToResourceDeniedException extends RuntimeException {
    public AccessToResourceDeniedException(String message) {
        super(message);
    }
}
