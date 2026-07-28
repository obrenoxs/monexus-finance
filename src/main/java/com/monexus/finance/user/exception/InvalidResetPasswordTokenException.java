package com.monexus.finance.user.exception;

public class InvalidResetPasswordTokenException extends RuntimeException {

    public InvalidResetPasswordTokenException(String message) {
        super(message);
    }
}
