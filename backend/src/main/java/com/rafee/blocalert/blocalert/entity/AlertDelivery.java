package com.rafee.blocalert.blocalert.entity;

import com.rafee.blocalert.blocalert.entity.enums.AlertChannel;
import com.rafee.blocalert.blocalert.entity.enums.AlertChannelStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "alert_deliveries")
public class AlertDelivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alert_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Alert alert;

    @Enumerated(EnumType.STRING)
    @Column(name = "alert_channel", nullable = false)
    private AlertChannel alertChannel;

    @Enumerated(EnumType.STRING)
    @Column(name = "alert_status", nullable = false)
    private AlertChannelStatus alertStatus;

    @CreationTimestamp
    @Column(name = "send_at", nullable = false, columnDefinition = "TIMESTAMP(0)")
    private LocalDateTime sendAt;

    @Column(name = "delivered_at", columnDefinition = "TIMESTAMP(0)")
    private LocalDateTime deliveredAt;

}
