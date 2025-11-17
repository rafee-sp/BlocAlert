package com.rafee.blocalert.blocalert.repository;

import com.rafee.blocalert.blocalert.entity.AlertDelivery;
import com.rafee.blocalert.blocalert.entity.enums.AlertChannelStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface AlertDeliveryRepository extends JpaRepository<AlertDelivery, Long> {

    @Modifying
    @Query("Update AlertDelivery a SET a.alertStatus = ?2 WHERE a.alert.id=?1")
    void updateDeliveryStatus(Long alertId, AlertChannelStatus status);
}
