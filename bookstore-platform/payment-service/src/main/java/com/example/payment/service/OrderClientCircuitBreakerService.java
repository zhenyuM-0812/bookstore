package com.example.payment.service;

import com.example.payment.client.OrderClient;
import com.example.payment.client.dto.OrderSummaryResponse;
import com.example.payment.exception.OrderServiceUnavailableException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderClientCircuitBreakerService {

    private final OrderClient orderClient;

    private final CircuitBreakerFactory<?, ?>
            circuitBreakerFactory;


    public OrderSummaryResponse getOrderById(
            Long orderId) {

        return circuitBreakerFactory
                .create("orderService")
                .run(
                        () -> orderClient.getOrderById(
                                orderId
                        ),
                        throwable -> fallback(
                                orderId,
                                throwable
                        )
                );
    }


    private OrderSummaryResponse fallback(
            Long orderId,
            Throwable throwable) {

        if (throwable instanceof FeignException feignException
                && feignException.status() >= 400
                && feignException.status() < 500) {

            throw feignException;
        }

        log.error(
                "Order service is unavailable when retrieving orderId={}",
                orderId,
                throwable
        );

        throw new OrderServiceUnavailableException(
                "Order service is temporarily unavailable"
        );
    }
}
