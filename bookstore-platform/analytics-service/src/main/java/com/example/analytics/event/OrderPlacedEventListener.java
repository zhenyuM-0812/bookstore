package com.example.analytics.event;

import com.example.analytics.service.OrderEventProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderPlacedEventListener {

    private final OrderEventProcessingService orderEventProcessingService;

    @KafkaListener(
            topics = "${app.kafka.topics.order-placed}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handleOrderPlaced(
            OrderPlacedEvent event) {

        log.info(
                "Received OrderPlacedEvent for analytics: eventId={}, orderId={}",
                event.getEventId(),
                event.getOrderId()
        );

        orderEventProcessingService.process(event);
    }
}
