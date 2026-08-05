package com.example.payment.client;

import com.example.payment.client.dto.OrderSummaryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "order-service",
        url = "${clients.order-service.url}"
)
public interface OrderClient {

    @GetMapping("/api/orders/{id}")
    OrderSummaryResponse getOrderById(
            @PathVariable("id") Long orderId
    );
}