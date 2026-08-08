package com.example.order.exception;

public class BookServiceUnavailableException
        extends RuntimeException {

    public BookServiceUnavailableException(
            String message) {

        super(message);
    }
}
