package com.rafee.blocalert.blocalert.service.impl;

import com.rafee.blocalert.blocalert.DTO.internal.AlertDeliveryResult;
import com.rafee.blocalert.blocalert.entity.Alert;
import com.rafee.blocalert.blocalert.entity.AlertDelivery;
import com.rafee.blocalert.blocalert.entity.enums.AlertChannel;
import com.rafee.blocalert.blocalert.entity.enums.AlertChannelStatus;
import com.rafee.blocalert.blocalert.repository.AlertDeliveryRepository;
import com.rafee.blocalert.blocalert.service.AlertDeliveryService;
import com.rafee.blocalert.blocalert.service.AlertService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertDeliveryServiceImpl implements AlertDeliveryService {

    private final AlertDeliveryRepository alertDeliveryRepository;
    private final AlertService alertService;

    @Override
    public void recordAlertDelivery(AlertChannel channel, AlertDeliveryResult alertDeliveryResult) {

        try {

            Alert alert = alertService.getAlertById(alertDeliveryResult.alertId());

            AlertDelivery alertDelivery = new AlertDelivery();
            alertDelivery.setAlert(alert);
            alertDelivery.setAlertChannel(channel);
            alertDelivery.setSendAt(alertDeliveryResult.triggeredAt());
            alertDelivery.setAlertStatus(alertDeliveryResult.status());

            if (alertDeliveryResult.status() == AlertChannelStatus.DELIVERED) {
                alertDelivery.setDeliveredAt(LocalDateTime.now());
            }

            alertDeliveryRepository.save(alertDelivery);

            log.debug("AlertDelivery record added for {} {}", channel, alertDeliveryResult.alertId());

        } catch (Exception e) {
            log.error("Failed to record alert for {} {}", channel, alertDeliveryResult.alertId());
        }
    }

    @Transactional
    @Override
    public void recordAlertDeliveries(AlertChannel channel, List<AlertDeliveryResult> deliveryResults) {

        log.info("recordWebsocketDeliveries called with {} results", deliveryResults.size());

        List<Long> alertIds = deliveryResults.stream()
                .map(AlertDeliveryResult::alertId)
                .distinct()
                .toList();

        Map<Long, Alert> alertMap = alertService.getAlertsByIds(alertIds)
                .stream()
                .collect(Collectors.toMap(Alert::getId, Function.identity()));

        List<AlertDelivery> alertDeliveryList = new ArrayList<>();

        for (AlertDeliveryResult result : deliveryResults) {

            Alert alert = alertMap.get(result.alertId());

            if (alert == null) {
                log.warn("Alert is null for alertId : {}", result.alertId());
                continue;
            }

            AlertDelivery alertDelivery = new AlertDelivery();
            alertDelivery.setAlert(alert);
            alertDelivery.setAlertChannel(channel);
            alertDelivery.setSendAt(result.triggeredAt());
            alertDelivery.setAlertStatus(result.status());

            if (result.status() == AlertChannelStatus.DELIVERED) {
                alertDelivery.setDeliveredAt(LocalDateTime.now());
            }

            alertDeliveryList.add(alertDelivery);
        }

        if (!alertDeliveryList.isEmpty()) {
            alertDeliveryRepository.saveAll(alertDeliveryList);
        } else {
            log.warn("No alert deliver entities found");
        }

        log.info("delivery records batch inserted {}", alertDeliveryList.size());
    }

    @Transactional
    @Override
    public void updateDeliveryStatus(Long alertId, AlertChannelStatus status) {
        alertDeliveryRepository.updateDeliveryStatus(alertId, status);
    }
}
