package com.example.notification.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderPlacedEvent {

    private String eventId;
    private Long orderId;
    private Long userId;
    private BigDecimal totalPrice;
    private String status;
    private LocalDateTime placedAt;
}