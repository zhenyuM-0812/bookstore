package com.example.order.service;

import com.example.order.dto.CreateOrderRequest;
import com.example.order.dto.OrderResponse;

import java.util.List;

public interface OrderService {

    OrderResponse createOrder(
            Long userId,
            CreateOrderRequest request
    );

    OrderResponse getOrderById(
            Long orderId,
            Long currentUserId,
            boolean isAdmin
    );

    List<OrderResponse> getCurrentUserOrders(
            Long userId
    );

    List<OrderResponse> getAllOrders();

    OrderResponse cancelOrder(
            Long orderId,
            Long currentUserId,
            boolean isAdmin
    );

    void markOrderAsPaid(Long orderId);
}