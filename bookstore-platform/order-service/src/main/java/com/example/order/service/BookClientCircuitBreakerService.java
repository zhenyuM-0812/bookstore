package com.example.order.service;

import com.example.order.client.BookClient;
import com.example.order.client.dto.BookStockResponse;
import com.example.order.client.dto.ReserveStockRequest;
import com.example.order.exception.BookServiceUnavailableException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookClientCircuitBreakerService {

    private final BookClient bookClient;

    private final CircuitBreakerFactory<?, ?>
            circuitBreakerFactory;


    public BookStockResponse reserveStock(
            Long bookId,
            ReserveStockRequest request) {

        return circuitBreakerFactory
                .create("bookService")
                .run(
                        () -> bookClient.reserveStock(
                                bookId,
                                request
                        ),
                        throwable -> fallback(
                                bookId,
                                throwable
                        )
                );
    }


    private BookStockResponse fallback(
            Long bookId,
            Throwable throwable) {

        if (throwable
                instanceof FeignException.Conflict conflict) {

            throw conflict;
        }

        log.error(
                "Book service is unavailable when reserving stock for bookId={}",
                bookId,
                throwable
        );

        throw new BookServiceUnavailableException(
                "Book service is temporarily unavailable"
        );
    }
}