package com.example.analytics.service;

import com.example.analytics.event.OrderPlacedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AnalyticsService {

    public void recordOrderPlaced(
            OrderPlacedEvent event) {

        log.info(
                "Order analytics recorded: eventId={}, orderId={}, userId={}, totalPrice={}, status={}, placedAt={}",
                event.getEventId(),
                event.getOrderId(),
                event.getUserId(),
                event.getTotalPrice(),
                event.getStatus(),
                event.getPlacedAt()
        );
    }
}
