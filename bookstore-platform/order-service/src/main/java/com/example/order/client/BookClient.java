package com.example.order.client;

import com.example.order.client.dto.BookStockResponse;
import com.example.order.client.dto.ReserveStockRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "book-service",
        url = "${clients.book-service.url}"
)
public interface BookClient {

    @PostMapping("/api/books/{id}/stock/reserve")
    BookStockResponse reserveStock(
            @PathVariable("id") Long bookId,
            @RequestBody ReserveStockRequest request
    );
}