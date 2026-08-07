package com.example.notification.service;

import com.example.notification.event.OrderPlacedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService {

    public void sendOrderConfirmation(
            OrderPlacedEvent event) {

        log.info(
                "Order confirmation sent: userId={}, orderId={}, totalPrice={}, placedAt={}",
                event.getUserId(),
                event.getOrderId(),
                event.getTotalPrice(),
                event.getPlacedAt()
        );
    }
}