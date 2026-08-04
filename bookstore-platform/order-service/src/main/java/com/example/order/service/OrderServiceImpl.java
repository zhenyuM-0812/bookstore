package com.example.order.service;

import com.example.order.dto.CreateOrderRequest;
import com.example.order.dto.OrderItemResponse;
import com.example.order.dto.OrderResponse;
import com.example.order.entity.Order;
import com.example.order.entity.OrderItem;
import com.example.order.entity.OrderStatus;
import com.example.order.exception.InvalidOrderStatusException;
import com.example.order.exception.OrderAccessDeniedException;
import com.example.order.exception.ResourceNotFoundException;
import com.example.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;


    private OrderItemResponse toItemResponse(
            OrderItem item) {

        return OrderItemResponse.builder()
                .id(item.getId())
                .bookId(item.getBookId())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .build();
    }


    private OrderResponse toResponse(
            Order order) {

        List<OrderItemResponse> itemResponses =
                order.getItems()
                        .stream()
                        .map(this::toItemResponse)
                        .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .status(order.getStatus())
                .totalPrice(order.getTotalPrice())
                .createdAt(order.getCreatedAt())
                .items(itemResponses)
                .build();
    }


    @Override
    public OrderResponse createOrder(
            Long userId,
            CreateOrderRequest request) {

        throw new UnsupportedOperationException(
                "createOrder will be implemented after BookClient is added"
        );
    }


    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(
            Long orderId,
            Long currentUserId,
            boolean isAdmin) {

        Order order = findOrderOrThrow(orderId);

        validateOrderAccess(
                order,
                currentUserId,
                isAdmin
        );

        return toResponse(order);
    }


    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getCurrentUserOrders(
            Long userId) {

        return orderRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {

        return orderRepository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }


    @Override
    @Transactional
    public OrderResponse cancelOrder(
            Long orderId,
            Long currentUserId,
            boolean isAdmin) {

        Order order = findOrderOrThrow(orderId);

        validateOrderAccess(
                order,
                currentUserId,
                isAdmin
        );

        if (order.getStatus() == OrderStatus.SHIPPED) {
            throw new InvalidOrderStatusException(
                    "Shipped order "
                            + orderId
                            + " cannot be cancelled"
            );
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            return toResponse(order);
        }

        order.setStatus(OrderStatus.CANCELLED);

        Order updatedOrder =
                orderRepository.save(order);

        return toResponse(updatedOrder);
    }


    private Order findOrderOrThrow(
            Long orderId) {

        return orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order with id "
                                        + orderId
                                        + " does not exist"
                        )
                );
    }


    private void validateOrderAccess(
            Order order,
            Long currentUserId,
            boolean isAdmin) {

        boolean isOwner =
                order.getUserId().equals(currentUserId);

        if (!isOwner && !isAdmin) {
            throw new OrderAccessDeniedException(
                    "You do not have permission to access order "
                            + order.getId()
            );
        }
    }
}