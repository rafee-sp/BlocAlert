package com.rafee.blocalert.blocalert.DTO.internal;

import com.rafee.blocalert.blocalert.entity.Alert;
import com.rafee.blocalert.blocalert.entity.User;
import com.rafee.blocalert.blocalert.entity.enums.AlertCondition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CachedAlert {

    private Long alertId;
    private Long userId;
    private String cryptoId;
    private BigDecimal thresholdValue;
    private AlertCondition alertCondition;
    private boolean notificationWebsocket;
    private boolean notificationEmail;
    private boolean notificationSms;

    public static CachedAlert from(Alert alert, Long userId) {
        return new CachedAlert(
                alert.getId(),
                userId,
                alert.getCryptoId(),
                alert.getThresholdValue(),
                alert.getCondition(),
                alert.getNotificationWebsocket(),
                alert.getNotificationEmail(),
                alert.getNotificationSms()
        );
    }

}
