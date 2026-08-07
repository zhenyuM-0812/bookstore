package com.example.order.event;


import com.example.order.service.PaymentEventProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {


    private final PaymentEventProcessingService paymentEventProcessingService;

    @KafkaListener(
            topics = "${app.kafka.topics.payment-completed}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handlePaymentCompleted(
            PaymentCompletedEvent event) {

        log.info(
                "Received PaymentCompletedEvent: eventId={}, orderId={}, paymentId={}",
                event.getEventId(),
                event.getOrderId(),
                event.getPaymentId()
        );

        if (!"SUCCESS".equals(event.getStatus())) {

            log.warn(
                    "Ignoring payment event {} because status is {}",
                    event.getEventId(),
                    event.getStatus()
            );

            return;
        }


        paymentEventProcessingService.process(event);
    }
}