package com.example.analytics.service;

import com.example.analytics.entity.ProcessedEvent;
import com.example.analytics.event.OrderPlacedEvent;
import com.example.analytics.repository.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderEventProcessingService {

    private final ProcessedEventRepository processedEventRepository;
    private final AnalyticsService analyticsService;

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

        analyticsService.recordOrderPlaced(event);
    }
}
