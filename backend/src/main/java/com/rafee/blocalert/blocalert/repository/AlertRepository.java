package com.rafee.blocalert.blocalert.repository;

import com.rafee.blocalert.blocalert.entity.Alert;
import com.rafee.blocalert.blocalert.entity.enums.AlertCondition;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {

    @Transactional
    @Modifying
    @Query("UPDATE Alert a SET a.isActive = false, a.isTriggered = true, a.triggeredAt = ?2 WHERE a.id IN ?1")
    void updateAlertAsTriggered(List<Long> alertIds, LocalDateTime dateTime);

    @Query("SELECT a FROM Alert a where a.user.id = ?1 AND a.isActive = true AND ( ?2 IS NULL OR a.cryptoId = ?2) ORDER BY a.createdAt DESC")
    Page<Alert> getActiveAlerts(Long userId, String cryptoId, Pageable pageable);

    @Query("SELECT a.id FROM Alert a WHERE a.user.id = ?1 AND a.isActive = false AND ( ?2 IS NULL OR a.cryptoId = ?2) ORDER BY a.createdAt DESC")
    Page<Long> getPastAlertIds(Long userId, String cryptoId, Pageable pageable);

    @Query("SELECT a FROM Alert a LEFT JOIN FETCH a.alertDeliveries WHERE a.id IN ?1 ORDER BY a.createdAt DESC")
    List<Alert> getPastAlertsByIds(List<Long> ids);

    @Query("SELECT COUNT(a) FROM Alert a WHERE a.user.id = ?1 AND a.isActive = true")
    int countActiveAlertsByUserId(Long id);

    @Query("SELECT COUNT(a) > 0 FROM Alert a WHERE a.user.id = ?1 AND a.cryptoId = ?2 AND a.condition = ?3 AND a.thresholdValue = ?4 AND a.isActive = true")
    boolean existsByAlert(Long userId, String cryptoId, AlertCondition condition, BigDecimal thresholdValue);
}
