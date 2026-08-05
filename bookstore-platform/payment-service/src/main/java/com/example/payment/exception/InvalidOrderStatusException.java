package com.example.payment.exception;

public class InvalidOrderStatusException
        extends RuntimeException {

    public InvalidOrderStatusException(String message) {
        super(message);
    }
}
