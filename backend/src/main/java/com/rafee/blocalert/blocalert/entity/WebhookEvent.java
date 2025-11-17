package com.rafee.blocalert.blocalert.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(name = "webhook_events" , indexes = {
        @Index(name = "idx_webhook_event_id", columnList = "stripe_event_id")
})
public class WebhookEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stripe_event_id", nullable = false, length = 100, unique = true)
    private String eventId;

    @Column(name = "stripe_event_type", nullable = false, length = 100)
    private String eventType;

    @Column(name = "is_processed", nullable = false)
    private Boolean isProcessed = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP(0)")
    private LocalDateTime createdAt;

    //payload for debugging

}
