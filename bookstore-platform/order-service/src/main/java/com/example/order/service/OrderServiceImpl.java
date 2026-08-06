package com.example.order.service;

import com.example.order.client.dto.BookStockResponse;
import com.example.order.client.BookClient;
import com.example.order.client.dto.ReserveStockRequest;
import com.example.order.dto.CreateOrderRequest;
import com.example.order.dto.OrderItemRequest;
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

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final BookClient bookClient;


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
    @Transactional
    public OrderResponse createOrder(
            Long userId,
            CreateOrderRequest request) {

        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalPrice(BigDecimal.ZERO);

        BigDecimal totalPrice = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.getItems()) {

            ReserveStockRequest reserveRequest =
                    new ReserveStockRequest(
                            itemRequest.getQuantity()
                    );

            BookStockResponse bookStock =
                    bookClient.reserveStock(
                            itemRequest.getBookId(),
                            reserveRequest
                    );
            OrderItem orderItem = new OrderItem();
            orderItem.setBookId(bookStock.getBookId());
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setUnitPrice(bookStock.getPrice());

            order.addItem(orderItem);

            BigDecimal itemTotal =
                    bookStock.getPrice()
                            .multiply(
                                    BigDecimal.valueOf(
                                            itemRequest.getQuantity()
                                    )
                            );

            totalPrice = totalPrice.add(itemTotal);

        }

        order.setTotalPrice(totalPrice);
        Order saveOrder = orderRepository.save(order);

        return toResponse(saveOrder);



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


    @Override
    @Transactional
    public void markOrderAsPaid(Long orderId){
        Order order = findOrderOrThrow(orderId);

        if (order.getStatus() == OrderStatus.PAID) {
            return;
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new InvalidOrderStatusException(
                    "Order "
                            + orderId
                            + " cannot be marked as paid because its status is "
                            + order.getStatus()
            );
        }

        order.setStatus(OrderStatus.PAID);

        orderRepository.save(order);
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