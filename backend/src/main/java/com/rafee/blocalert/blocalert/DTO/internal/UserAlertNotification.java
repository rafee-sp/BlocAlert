package com.rafee.blocalert.blocalert.DTO.internal;

import com.rafee.blocalert.blocalert.entity.enums.AlertCondition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAlertNotification {

    private Long alertId;
    private Long userId;
    private String cryptoId;
    private String cryptoName;
    private String cryptoImage;
    private BigDecimal thresholdValue;
    private AlertCondition alertCondition;
    private BigDecimal currentPrice;
    private boolean isWebsocketSubscribed;
    private boolean isSmsSubscribed;
    private boolean isEmailSubscribed;


    public static UserAlertNotification fromAlert(CachedAlert alert, CryptoMarketLite cryptoMarketLite) {

        return new UserAlertNotification(
                alert.getAlertId(),
                alert.getUserId(),
                cryptoMarketLite.id(),
                cryptoMarketLite.name(),
                cryptoMarketLite.image(),
                alert.getThresholdValue(),
                alert.getAlertCondition(),
                cryptoMarketLite.current_price(),
                alert.isNotificationWebsocket(),
                alert.isNotificationSms(),
                alert.isNotificationEmail()
        );
    }

}
