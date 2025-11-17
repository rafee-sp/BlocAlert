package com.rafee.blocalert.blocalert.DTO.request;

import com.rafee.blocalert.blocalert.entity.enums.AlertCondition;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AlertRequest {

    @NotBlank(message = "Crypto Id is required")
    private String cryptoId;

    @NotNull(message = "Condition is required")
    private AlertCondition condition;

    @NotNull(message = "Threshold value is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Threshold must be greater than 0")
    private BigDecimal thresholdValue;

    @NotNull(message = "Notification websocket flag is required")
    private Boolean notificationWebsocket;

    @NotNull(message = "Notification Email is required")
    private Boolean notificationEmail;

    @NotNull(message = "Notification Sms is required")
    private Boolean notificationSms;
}
