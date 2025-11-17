package com.rafee.blocalert.blocalert.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rafee.blocalert.blocalert.DTO.internal.AlertDeliveryResult;
import com.rafee.blocalert.blocalert.DTO.internal.CachedAlert;
import com.rafee.blocalert.blocalert.DTO.internal.CryptoMarketLite;
import com.rafee.blocalert.blocalert.DTO.internal.CryptoMarketData;
import com.rafee.blocalert.blocalert.DTO.request.AlertRequest;
import com.rafee.blocalert.blocalert.DTO.response.ActiveAlertResponse;
import com.rafee.blocalert.blocalert.DTO.internal.UserAlertNotification;
import com.rafee.blocalert.blocalert.DTO.response.PastAlertResponse;
import com.rafee.blocalert.blocalert.config.AppConfig;
import com.rafee.blocalert.blocalert.entity.Alert;
import com.rafee.blocalert.blocalert.entity.User;
import com.rafee.blocalert.blocalert.entity.enums.AlertCondition;
import com.rafee.blocalert.blocalert.entity.enums.UserRole;
import com.rafee.blocalert.blocalert.exception.AlertLimitExceedException;
import com.rafee.blocalert.blocalert.exception.DuplicateAlertException;
import com.rafee.blocalert.blocalert.exception.ResourceNotFoundException;
import com.rafee.blocalert.blocalert.repository.AlertRepository;
import com.rafee.blocalert.blocalert.service.*;
import com.rafee.blocalert.blocalert.utils.RedisKeys;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;
    private final UserService userService;
    private final CryptoService cryptoService;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;
    private final AppConfig appConfig;

    @Override
    public void addAlert(Long userId, AlertRequest request) {

        log.info("addAlert called for userId {}", userId);

        User user = userService.getUser(userId);

        checkUserLimit(user);

        checkAndThrowDuplicateAlert(userId, request);

        Alert alert = new Alert();
        alert.setUser(user);
        updateAlertFields(alert, request);

        alert = alertRepository.save(alert);

        cacheAlert(alert, userId);

    }

    @Override
    public void updateAlert(Long alertId, Long userId, AlertRequest request) throws AccessDeniedException {

        log.info("updateAlert called for alert {}, user {}", alertId, userId);

        Alert alert = alertRepository.findById(alertId).orElseThrow(() -> new ResourceNotFoundException("Alert not found " + alertId));

        if (!alert.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("User don't have access for this alert");
        }

        checkAndThrowDuplicateAlert(userId, request);

        updateAlertFields(alert, request);

        alert = alertRepository.save(alert);

        cacheAlert(alert, userId);

    }

    @Override
    @Transactional
    public void deleteAlert(Long alertId, Long userId) throws AccessDeniedException {

        log.info("deleteAlert called for alert {}, user {}", alertId, userId);

        Alert alert = alertRepository.findById(alertId).orElseThrow(() -> new ResourceNotFoundException("Alert not found " + alertId));

        if (!alert.getUser().getId().equals(userId)) {    // TODO : search by userId and alertId
            throw new AccessDeniedException("User don't have access for this alert");
        }

        alertRepository.delete(alert);

        redisService.hashDelField(RedisKeys.alertKey(alert.getCryptoId()), RedisKeys.alertId(alert.getId()));
    }

    @Override
    public Page<ActiveAlertResponse> getActiveAlerts(Long userId, String cryptoId, Pageable pageable) {

        log.info("getActiveAlerts called for userId {}, cryptoId {}", userId, cryptoId);

        Page<ActiveAlertResponse> alerts = (!StringUtils.hasText(cryptoId)) ? getPaginatedActiveAlerts(userId, pageable) : getPaginatedActiveAlertsOfCrypto(userId, cryptoId, pageable);

        log.info("alerts size : {}", alerts.getContent().size());

        return alerts;
    }

    @Override
    public Page<PastAlertResponse> getPastAlerts(Long userId, String cryptoId, Pageable pageable) {

        log.info("getPastAlerts called for userId {}, cryptoId {}", userId, cryptoId);

        Page<PastAlertResponse> alerts = (!StringUtils.hasText(cryptoId)) ? getPaginatedPastAlerts(userId, pageable) : getPaginatedPastAlertsOfCrypto(userId, cryptoId, pageable);

        log.info("past alerts size : {}", alerts.getContent().size());

        return alerts;
    }

    @Override
    public List<UserAlertNotification> evaluateAlerts(List<CryptoMarketLite> latestCrypto) {

        log.info("evaluateAlerts called");

        Set<String> cryptoIds = latestCrypto.stream()
                .map(crypto -> RedisKeys.alertKey(crypto.id()))
                .collect(Collectors.toSet());

        Map<String, Map<Object, Object>> redisCachedAlerts = redisService.hashGetAll(cryptoIds);

        return latestCrypto.stream()
                .flatMap(crypto -> {
                    Map<Object, Object> cachedAlerts = redisCachedAlerts.getOrDefault(RedisKeys.alertKey(crypto.id()), Collections.emptyMap());
                    return cachedAlerts.values().stream()
                            .map(value -> parseAlert((String) value))
                            .filter(Objects::nonNull)
                            .filter(alert -> shouldTriggerAlert(crypto, alert))
                            .map(alert -> UserAlertNotification.fromAlert(alert, crypto));
                })
                .toList();
    }

    @Override
    public Alert getAlertById(Long alertId) {
        return alertRepository.findById(alertId).orElseThrow(() -> new ResourceNotFoundException("Alert not found with id " + alertId));
    }

    @Override
    public List<Alert> getAlertsByIds(List<Long> alertIds) {

        if (alertIds == null || alertIds.isEmpty()) {
            throw new IllegalArgumentException("Alert Ids are not valid");
        }
        return alertRepository.findAllById(alertIds);
    }

    @Async
    @Override
    public void setAlertAsTriggered(List<AlertDeliveryResult> deliveryResults) {

        log.info("setAlertAsTriggered called for deliveries : {}", deliveryResults.size());

        List<Long> alertIds = deliveryResults.stream()
                .map(AlertDeliveryResult::alertId)
                .distinct()
                .toList();

        alertRepository.updateAlertAsTriggered(alertIds, LocalDateTime.now());
    }

    @Async
    @Override
    public void cleanupRedisAlerts(List<UserAlertNotification> alerts) {

        if (alerts.isEmpty()) {
            return;
        }

        try {
            Map<String, List<Long>> alertsByCrypto = alerts.stream()
                    .collect(Collectors.groupingBy(
                            UserAlertNotification::getCryptoId,
                            Collectors.mapping(UserAlertNotification::getAlertId, Collectors.toList())
                    ));

            alertsByCrypto.forEach((cryptoId, alertIds) -> {
                if (alertIds.isEmpty()) return;

                try {
                    String key = RedisKeys.alertKey(cryptoId);
                    Object[] fields = alertIds.stream()
                            .map(RedisKeys::alertId)
                            .toArray();

                    redisService.hashDelFields(key, fields);
                    log.debug("Removed {} alerts from crypto: {}", alertIds.size(), cryptoId);

                } catch (Exception e) {
                    log.error("Failed to delete alerts from Redis for crypto: {}. Alert IDs: {}. Error: {}", cryptoId, alertIds, e.getMessage(), e);
                }
            });
            log.info("Completed Redis cleanup for {} alerts across {} cryptos", alerts.size(), alertsByCrypto.size());

        } catch (Exception e) {
            log.error("Critical error during Redis alert cleanup: {}", e.getMessage(), e);
        }
    }

    private Page<PastAlertResponse> getPaginatedPastAlertsOfCrypto(Long userId, String cryptoId, Pageable pageable) {

        log.info("getPaginatedPastAlertsOfCrypto called for {}, userId {}", cryptoId, userId);

        Page<Long> alertIdsPage = alertRepository.getPastAlertIds(userId, cryptoId, pageable);

        if (alertIdsPage.getContent().isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        List<Alert> alertList = alertRepository.getPastAlertsByIds(alertIdsPage.getContent());

        return mapToPastAlertResponse(alertList, Set.of(cryptoId), pageable, alertIdsPage.getTotalElements());

    }

    private Page<PastAlertResponse> getPaginatedPastAlerts(Long userId, Pageable pageable) {

        log.info("getPaginatedPastAlerts called for userId {}", userId);

        Page<Long> alertIdsPage = alertRepository.getPastAlertIds(userId, null, pageable);

        if (alertIdsPage.getContent().isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        List<Alert> alertList = alertRepository.getPastAlertsByIds(alertIdsPage.getContent());

        Set<String> cryptoIds = alertList.stream().map(Alert::getCryptoId).collect(Collectors.toSet());

        return mapToPastAlertResponse(alertList, cryptoIds, pageable, alertIdsPage.getTotalElements());

    }

    private Page<ActiveAlertResponse> getPaginatedActiveAlertsOfCrypto(Long userId, String cryptoId, Pageable pageable) {

        log.info("getPaginatedActiveAlertsOfCrypto called for {}, userId {}", cryptoId, userId);

        Page<Alert> alertsPage = alertRepository.getActiveAlerts(userId, cryptoId, pageable);

        if (alertsPage.getContent().isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        return mapToActiveAlertResponse(alertsPage.getContent(), Set.of(cryptoId), pageable, alertsPage.getTotalElements());

    }

    private Page<ActiveAlertResponse> getPaginatedActiveAlerts(Long userId, Pageable pageable) {

        log.info("getPaginatedActiveAlerts called for userId {}", userId);

        Page<Alert> alertsPage = alertRepository.getActiveAlerts(userId, null, pageable);

        if (alertsPage.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        List<Alert> alertList = alertsPage.getContent();

        Set<String> cryptoIds = alertList.stream().map(Alert::getCryptoId).collect(Collectors.toSet());

        return mapToActiveAlertResponse(alertList, cryptoIds, pageable, alertsPage.getTotalElements());

    }

    private Page<ActiveAlertResponse> mapToActiveAlertResponse(List<Alert> activeAlertsList, Set<String> cryptoIds, Pageable pageable, Long totalElements) {

        Map<String, CryptoMarketData> cryptoDataMap = getAndValidateCryptoData(cryptoIds);

        List<ActiveAlertResponse> activeAlertsResponse = activeAlertsList.stream().map(alert -> {
                    CryptoMarketData crypto = cryptoDataMap.get(alert.getCryptoId());

                    return new ActiveAlertResponse(alert, crypto);
                })
                .toList();

        return new PageImpl<>(activeAlertsResponse, pageable, totalElements);

    }

    private Page<PastAlertResponse> mapToPastAlertResponse(List<Alert> pastAlertsList, Set<String> cryptoIds, Pageable pageable, Long totalElements) {

        Map<String, CryptoMarketData> cryptoDataMap = getAndValidateCryptoData(cryptoIds);

        List<PastAlertResponse> pastAlertsResponse = pastAlertsList.stream().map(alert -> {
                    CryptoMarketData crypto = cryptoDataMap.get(alert.getCryptoId());

                    return PastAlertResponse.from(alert, crypto);
                })
                .toList();

        return new PageImpl<>(pastAlertsResponse, pageable, totalElements);

    }

    private void checkAndThrowDuplicateAlert(Long userId, AlertRequest request) {

        if (alertRepository.existsByAlert(userId, request.getCryptoId(), request.getCondition(), request.getThresholdValue())) {
            throw new DuplicateAlertException("Alert already exists for this condition");
        }
    }

    private void checkUserLimit(User user) {

        if (user.isSubscribed() && user.getRole() == UserRole.ROLE_PREMIUM_USER) return;

        int activeAlerts = alertRepository.countActiveAlertsByUserId(user.getId());

        if (activeAlerts >= appConfig.getFreeAlertLimit()) {
            throw new AlertLimitExceedException("Alert Limit exceeded");
        }
    }

    private void updateAlertFields(Alert alert, AlertRequest request) {

        alert.setCryptoId(request.getCryptoId());
        alert.setCondition(request.getCondition());
        alert.setThresholdValue(request.getThresholdValue());
        alert.setNotificationWebsocket(request.getNotificationWebsocket());
        alert.setNotificationEmail(request.getNotificationEmail());
        alert.setNotificationSms(request.getNotificationSms());
    }

    private boolean shouldTriggerAlert(CryptoMarketLite crypto, CachedAlert alert) {

        BigDecimal thresholdPrice = alert.getThresholdValue();
        BigDecimal currentPrice = crypto.current_price();

        int decimals = thresholdPrice.scale();
        BigDecimal roundedCurrent = currentPrice.setScale(decimals, RoundingMode.DOWN);
        BigDecimal roundedThreshold = thresholdPrice.setScale(decimals, RoundingMode.DOWN);

        return switch (alert.getAlertCondition()) {

            case AlertCondition.PRICE_ABOVE -> roundedCurrent.compareTo(roundedThreshold) > 0;
            case AlertCondition.PRICE_BELOW -> roundedCurrent.compareTo(roundedThreshold) < 0;
            case AlertCondition.PRICE_EQUALS -> roundedCurrent.compareTo(roundedThreshold) == 0;
        };
    }

    private CachedAlert parseAlert(String alertJson) {

        try {
            return objectMapper.readValue(alertJson, CachedAlert.class);
        } catch (Exception e) {
            log.error("Error while parsing the cached alert {}", alertJson, e);
            return null;
        }
    }

    private Map<String, CryptoMarketData> getAndValidateCryptoData(Set<String> cryptoIds) {

        Map<String, CryptoMarketData> cryptoDataMap = cryptoService.getMultipleCryptoData(cryptoIds);

        List<String> missingIds = cryptoIds.stream()
                .filter(id -> !cryptoDataMap.containsKey(id))
                .toList();

        if (!missingIds.isEmpty()) {
            log.error("Crypto Data not found for Id : {}", missingIds);
            throw new ResourceNotFoundException("Crypto data not found for " + missingIds.size() + " ID(s): " + String.join(", ", missingIds));
        }

        return cryptoDataMap;
    }

    private void cacheAlert(Alert alert, Long userId) {

        CachedAlert cachedAlert = CachedAlert.from(alert, userId);
        // cache in redis
        redisService.hashSet(RedisKeys.alertKey(alert.getCryptoId()), RedisKeys.alertId(alert.getId()), cachedAlert);
    }

}
