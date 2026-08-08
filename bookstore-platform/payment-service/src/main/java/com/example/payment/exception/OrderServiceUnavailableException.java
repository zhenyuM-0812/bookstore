package com.example.payment.exception;

public class OrderServiceUnavailableException
        extends RuntimeException {

    public OrderServiceUnavailableException(
            String message) {

        super(message);
    }
}
