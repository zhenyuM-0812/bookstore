package com.example.order.controller;

import com.example.order.dto.CreateOrderRequest;
import com.example.order.dto.OrderResponse;
import com.example.order.security.AuthenticatedUser;
import com.example.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;


    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @AuthenticationPrincipal
            AuthenticatedUser currentUser,
            @Valid @RequestBody
            CreateOrderRequest request) {

        OrderResponse response =
                orderService.createOrder(
                        currentUser.getUserId(),
                        request
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }


    @GetMapping
    public ResponseEntity<List<OrderResponse>>
    getCurrentUserOrders(
            @AuthenticationPrincipal
            AuthenticatedUser currentUser) {

        List<OrderResponse> response =
                orderService.getCurrentUserOrders(
                        currentUser.getUserId()
                );

        return ResponseEntity.ok(response);
    }


    @GetMapping("/all")
    public ResponseEntity<List<OrderResponse>>
    getAllOrders() {

        List<OrderResponse> response =
                orderService.getAllOrders();

        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable Long id,
            @AuthenticationPrincipal
            AuthenticatedUser currentUser,
            Authentication authentication) {

        OrderResponse response =
                orderService.getOrderById(
                        id,
                        currentUser.getUserId(),
                        isAdmin(authentication)
                );

        return ResponseEntity.ok(response);
    }


    @PutMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable Long id,
            @AuthenticationPrincipal
            AuthenticatedUser currentUser,
            Authentication authentication) {

        OrderResponse response =
                orderService.cancelOrder(
                        id,
                        currentUser.getUserId(),
                        isAdmin(authentication)
                );

        return ResponseEntity.ok(response);
    }


    private boolean isAdmin(
            Authentication authentication) {

        return authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority()
                                .equals("ROLE_ADMIN")
                );
    }
}