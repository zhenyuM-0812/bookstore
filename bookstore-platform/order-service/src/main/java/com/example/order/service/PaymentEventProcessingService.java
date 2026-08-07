package com.example.order.service;

import com.example.order.entity.ProcessedEvent;
import com.example.order.event.PaymentCompletedEvent;
import com.example.order.repository.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentEventProcessingService {

    private final ProcessedEventRepository processedEventRepository;
    private final OrderService orderService;

    @Transactional
    public void process(
            PaymentCompletedEvent event) {

        if (processedEventRepository.existsById(
                event.getEventId())) {

            log.info(
                    "Skipping duplicate PaymentCompletedEvent: eventId={}",
                    event.getEventId()
            );

            return;
        }

        ProcessedEvent processedEvent =
                new ProcessedEvent(
                        event.getEventId(),
                        "PaymentCompletedEvent"
                );

        processedEventRepository.save(processedEvent);

        orderService.markOrderAsPaid(
                event.getOrderId()
        );

        log.info(
                "Order {} was marked as PAID",
                event.getOrderId()
        );
    }
}