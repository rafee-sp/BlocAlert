package com.rafee.blocalert.blocalert.repository;

import com.rafee.blocalert.blocalert.DTO.response.SubscriptionDetailResponse;
import com.rafee.blocalert.blocalert.entity.Subscription;
import com.rafee.blocalert.blocalert.entity.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findByUser_IdAndSessionId(Long userId, String sessionId);

    Optional<Subscription> findByUser_IdAndSubscriptionId(Long id, String subscriptionId);

    Optional<Subscription> findByUser_IdAndSubscriptionStatus(Long id, SubscriptionStatus subscriptionStatus);

    @Query("SELECT new com.rafee.blocalert.blocalert.DTO.response.SubscriptionDetailResponse(s.id, s.subscriptionStatus, s.currentSubscriptionEnd) " +
            "FROM Subscription s WHERE s.user.id = ?1 ORDER BY s.createdAt DESC")
    Optional<SubscriptionDetailResponse> getRecentUserSubscription(Long userId);

    boolean existsByUser_IdAndSubscriptionId(Long id, String subscriptionId);
}
