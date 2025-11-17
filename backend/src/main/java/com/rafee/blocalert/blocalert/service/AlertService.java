package com.rafee.blocalert.blocalert.service;

import com.rafee.blocalert.blocalert.DTO.internal.AlertDeliveryResult;
import com.rafee.blocalert.blocalert.DTO.internal.CryptoMarketLite;
import com.rafee.blocalert.blocalert.DTO.request.AlertRequest;
import com.rafee.blocalert.blocalert.DTO.response.ActiveAlertResponse;
import com.rafee.blocalert.blocalert.DTO.internal.UserAlertNotification;
import com.rafee.blocalert.blocalert.DTO.response.PastAlertResponse;
import com.rafee.blocalert.blocalert.entity.Alert;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface AlertService {

    void addAlert(Long userId, AlertRequest alertRequest);

    void updateAlert(Long alertId, Long userId, @Valid AlertRequest request) throws AccessDeniedException;

    void deleteAlert(Long id, Long userId) throws AccessDeniedException;

    Page<ActiveAlertResponse> getActiveAlerts(Long userId, String cryptoId, Pageable pageable);

    Page<PastAlertResponse> getPastAlerts(Long userId, String cryptoId, Pageable pageable);

    List<UserAlertNotification> evaluateAlerts(List<CryptoMarketLite> latestCrypto);

    Alert getAlertById(Long alertId);

    List<Alert> getAlertsByIds(List<Long> alertIds);

    void setAlertAsTriggered(List<AlertDeliveryResult> deliveryResults);

    void cleanupRedisAlerts(List<UserAlertNotification> alerts);
}
