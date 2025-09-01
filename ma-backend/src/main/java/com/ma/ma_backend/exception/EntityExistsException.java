package com.ma.ma_backend.exception;

public class EntityExistsException extends RuntimeException {
    public EntityExistsException(String message) {
        super(message);
    }
}
