package com.rafee.blocalert.blocalert.service;

import com.auth0.exception.Auth0Exception;
import com.rafee.blocalert.blocalert.DTO.NewUserRequest;
import com.rafee.blocalert.blocalert.DTO.internal.UserContactInfo;
import com.rafee.blocalert.blocalert.DTO.request.UserRequest;
import com.rafee.blocalert.blocalert.DTO.response.UserResponse;
import com.rafee.blocalert.blocalert.entity.User;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.Map;
import java.util.Set;

public interface UserService {

    void createUser(NewUserRequest userRequest);

    Long getUserIdByAuth0Id(String auth0Id);

    UserResponse getUserDetails(Long userId);

    User getUser(Long userId);

    User getUserByStripeCustomerId(String customerId);

    void upgradeToPremium(Long userId);

    void downgradeUserToFree(Long id);

    void updateCustomerId(Long userId, String customerId);

    void updateUser(Long userId, UserRequest request);

    void resetPassword(Long userId) throws Auth0Exception;

    Map<Long, UserContactInfo> getUsersContactInfo(Set<Long> userIds);
}
