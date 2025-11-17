package com.rafee.blocalert.blocalert.repository;

import com.rafee.blocalert.blocalert.DTO.internal.UserContactInfo;
import com.rafee.blocalert.blocalert.DTO.response.UserResponse;
import com.rafee.blocalert.blocalert.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByAuth0Id(String auth0Id);

    @Query("SELECT new com.rafee.blocalert.blocalert.DTO.response.UserResponse(u.name, u.email, u.phoneNumber, u.role, u.themePreference) FROM User u WHERE u.id = ?1")
    Optional<UserResponse> getUserDetails(Long userId);

    Optional<User> findByAuth0Id(String auth0Id);

    Optional<User> findByStripeCustomerId(String customerId);

    @Query("SELECT new com.rafee.blocalert.blocalert.DTO.internal.UserContactInfo(u.id, u.email, u.phoneNumber, u.isSubscribed) FROM User u WHERE u.id IN ?1")
    List<UserContactInfo> findContactInfoByIds(Set<Long> userIds);
}
