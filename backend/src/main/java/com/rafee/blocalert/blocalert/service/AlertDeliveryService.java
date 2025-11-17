package com.rafee.blocalert.blocalert.service;

import com.rafee.blocalert.blocalert.DTO.internal.AlertDeliveryResult;
import com.rafee.blocalert.blocalert.entity.enums.AlertChannel;
import com.rafee.blocalert.blocalert.entity.enums.AlertChannelStatus;

import java.util.List;

public interface AlertDeliveryService {

    void recordAlertDelivery(AlertChannel channel, AlertDeliveryResult alertDeliveryResultList);

    void recordAlertDeliveries(AlertChannel channel, List<AlertDeliveryResult> alertDeliveryResultList);

    void updateDeliveryStatus(Long alertId, AlertChannelStatus status);
}
