package com.example.payment.event;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventPublisher {

    private final KafkaTemplate<
            String,
            PaymentCompletedEvent
            > kafkaTemplate;

    private final String paymentCompletedTopic;


    public PaymentEventPublisher(
            KafkaTemplate<
                    String,
                    PaymentCompletedEvent
                    > kafkaTemplate,
            @Value(
                    "${app.kafka.topics.payment-completed}"
            )
            String paymentCompletedTopic) {

        this.kafkaTemplate = kafkaTemplate;
        this.paymentCompletedTopic =
                paymentCompletedTopic;
    }


    public void publish(
            PaymentCompletedEvent event) {

        kafkaTemplate.send(
                paymentCompletedTopic,
                event.getOrderId().toString(),
                event
        );
    }
}