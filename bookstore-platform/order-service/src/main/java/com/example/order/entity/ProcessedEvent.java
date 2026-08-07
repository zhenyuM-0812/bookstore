package com.example.order.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "processed_event")
@Getter
@NoArgsConstructor
public class ProcessedEvent {

    @Id
    @Column(
            name = "event_id",
            nullable = false,
            length = 36,
            updatable = false
    )
    private String eventId;

    @Column(
            name = "event_type",
            nullable = false,
            length = 100,
            updatable = false
    )
    private String eventType;

    @CreationTimestamp
    @Column(
            name = "processed_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime processedAt;

    public ProcessedEvent(
            String eventId,
            String eventType) {

        this.eventId = eventId;
        this.eventType = eventType;
    }
}