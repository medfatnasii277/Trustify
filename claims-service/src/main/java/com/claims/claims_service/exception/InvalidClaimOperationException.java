package com.claims.claims_service.exception;

public class InvalidClaimOperationException extends RuntimeException {
    public InvalidClaimOperationException(String message) {
        super(message);
    }
}
