package com.example.notification.service;

import com.example.notification.entity.ProcessedEvent;
import com.example.notification.event.OrderPlacedEvent;
import com.example.notification.repository.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderEventProcessingService {

    private final ProcessedEventRepository processedEventRepository;
    private final NotificationService notificationService;

    @Transactional
    public void process(
            OrderPlacedEvent event) {

        if (processedEventRepository.existsById(
                event.getEventId())) {

            log.info(
                    "Skipping duplicate OrderPlacedEvent: eventId={}",
                    event.getEventId()
            );

            return;
        }

        ProcessedEvent processedEvent =
                new ProcessedEvent(
                        event.getEventId(),
                        "OrderPlacedEvent"
                );

        processedEventRepository.saveAndFlush(processedEvent);

        notificationService.sendOrderConfirmation(event);
    }
}
