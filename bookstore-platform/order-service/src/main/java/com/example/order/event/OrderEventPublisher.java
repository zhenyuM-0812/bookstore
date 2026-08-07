package com.example.order.event;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventPublisher {

    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;
    private final String orderPlacedTopic;

    public OrderEventPublisher(
            KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate,
            @Value("${app.kafka.topics.order-placed}")
            String orderPlacedTopic) {

        this.kafkaTemplate = kafkaTemplate;
        this.orderPlacedTopic = orderPlacedTopic;
    }

    public void publish(OrderPlacedEvent event) {

        kafkaTemplate.send(
                orderPlacedTopic,
                event.getOrderId().toString(),
                event
        );
    }
}