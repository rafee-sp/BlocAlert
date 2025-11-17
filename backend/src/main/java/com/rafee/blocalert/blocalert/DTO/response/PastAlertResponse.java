package com.rafee.blocalert.blocalert.DTO.response;

import com.rafee.blocalert.blocalert.DTO.internal.CryptoMarketData;
import com.rafee.blocalert.blocalert.entity.Alert;
import com.rafee.blocalert.blocalert.entity.AlertDelivery;
import com.rafee.blocalert.blocalert.entity.enums.AlertChannel;
import com.rafee.blocalert.blocalert.entity.enums.AlertChannelStatus;
import com.rafee.blocalert.blocalert.entity.enums.AlertCondition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PastAlertResponse {

    private Long id;
    private String cryptoName;
    private String cryptoImage;
    private String cryptoSymbol;
    private AlertCondition condition;
    private BigDecimal thresholdValue;
    private LocalDateTime createdAt;
    private boolean isTriggered;
    private LocalDateTime triggeredAt;
    private boolean notificationWebsocket;
    private boolean notificationEmail;
    private boolean notificationSms;
    private boolean websocketSent;
    private boolean smsSent;
    private boolean emailSent;


    public static PastAlertResponse from(Alert alert, CryptoMarketData crypto) {

        List<AlertDelivery> deliveries = alert.getAlertDeliveries();
        return new PastAlertResponse(
                alert.getId(),
                crypto.name(),
                crypto.image(),
                crypto.symbol(),
                alert.getCondition(),
                alert.getThresholdValue(),
                alert.getCreatedAt(),
                alert.getIsTriggered(),
                alert.getTriggeredAt(),
                alert.getNotificationWebsocket(),
                alert.getNotificationEmail(),
                alert.getNotificationSms(),
                isDelivered(deliveries, AlertChannel.WEBSOCKET),
                isDelivered(deliveries, AlertChannel.SMS),
                isDelivered(deliveries, AlertChannel.EMAIL)
        );
    }

    private static boolean isDelivered(List<AlertDelivery> deliveries, AlertChannel alertChannel) {
        return deliveries != null && deliveries.stream().anyMatch(delivery -> delivery.getAlertChannel() == alertChannel && delivery.getAlertStatus() == AlertChannelStatus.DELIVERED);
    }


}

