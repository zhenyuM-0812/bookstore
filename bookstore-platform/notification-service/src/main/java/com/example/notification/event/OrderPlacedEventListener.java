package com.example.notification.event;

import com.example.notification.service.OrderEventProcessingService;
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
                "Received OrderPlacedEvent: eventId={}, orderId={}, userId={}",
                event.getEventId(),
                event.getOrderId(),
                event.getUserId()
        );

        orderEventProcessingService.process(event);
    }
}