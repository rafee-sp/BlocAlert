package com.rafee.blocalert.blocalert.service.impl;

import com.rafee.blocalert.blocalert.DTO.NewUserRequest;
import com.rafee.blocalert.blocalert.DTO.internal.UserContactInfo;
import com.rafee.blocalert.blocalert.DTO.request.UserRequest;
import com.rafee.blocalert.blocalert.DTO.response.UserResponse;
import com.rafee.blocalert.blocalert.entity.User;
import com.rafee.blocalert.blocalert.entity.enums.UserRole;
import com.rafee.blocalert.blocalert.events.event.UserEmailEvent;
import com.rafee.blocalert.blocalert.events.event.enums.EmailEventType;
import com.rafee.blocalert.blocalert.exception.ResourceNotFoundException;
import com.rafee.blocalert.blocalert.repository.UserRepository;
import com.rafee.blocalert.blocalert.service.Auth0ManagementService;
import com.rafee.blocalert.blocalert.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final Auth0ManagementService auth0ManagementService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void createUser(NewUserRequest userRequest) {

        log.info("createUser called");

        boolean isUserExists = userRepository.existsByAuth0Id(userRequest.auth0Id());

        if(isUserExists)
            throw new IllegalStateException("User already present with the Auth0Id : " + userRequest.auth0Id());

        User newUser = new User();
        newUser.setAuth0Id(userRequest.auth0Id());
        newUser.setEmail(userRequest.email());
        if(StringUtils.hasText(userRequest.name())){
            newUser.setName(userRequest.name());
        }

        User savedUser = userRepository.save(newUser);

        eventPublisher.publishEvent(new UserEmailEvent(savedUser.getId(), EmailEventType.MAIL_VERIFICATION));
    }

    @Override
    public Long getUserIdByAuth0Id(String auth0Id) {

        log.debug("getUserIdByAuth0Id called for {}", auth0Id);

        if(!StringUtils.hasText(auth0Id))
            throw new IllegalArgumentException("Auth0Id is not valid " + auth0Id);

        User user =  userRepository.findByAuth0Id(auth0Id).orElseThrow(() -> new ResourceNotFoundException("User not found with Auth0Id : "+auth0Id));

        return user.getId();
    }

    @Override
    public UserResponse getUserDetails(Long userId) {

        log.debug("getUserDetails called for {}", userId);

        if(userId == null || userId <= 0) throw new IllegalArgumentException("User Id cannot be invalid");

        return userRepository.getUserDetails(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with id : "+ userId));

    }

    @Override
    public User getUserByStripeCustomerId(String customerId) {
       return userRepository.findByStripeCustomerId(customerId).orElseThrow(() -> new ResourceNotFoundException("User not found with stripe customer Id  "+ customerId));
    }

    @Override
    @Transactional
    public void updateCustomerId(Long userId, String customerId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with Id : "+userId));
        user.setStripeCustomerId(customerId);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateUser(Long userId, UserRequest request) {
        log.info("updateUser called for userId : {}",userId);
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with Id : "+userId));

        if (StringUtils.hasText(request.getPhoneNumber())) {
            user.setPhoneNumber(request.getPhoneNumber());
        }

        if (request.getTheme() != null) {
            user.setThemePreference(request.getTheme());
        }

        userRepository.save(user);
    }

    @Override
    public void resetPassword(Long userId) {
        log.info("forgotPassword called for userId : {}",userId);
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with Id : "+userId));

        eventPublisher.publishEvent(new UserEmailEvent(user.getId(), EmailEventType.MAIL_VERIFICATION));
    }

    @Override
    public Map<Long, UserContactInfo> getUsersContactInfo(Set<Long> userIds) {

        return userRepository.findContactInfoByIds(userIds)
                .stream()
                .collect(Collectors.toMap(UserContactInfo::id, Function.identity()));
    }

    @Override
    @Transactional
    public void upgradeToPremium(Long userId) {

        log.info("upgradeToPremium called");
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with Id : "+userId));
        user.setSubscribed(true);
        user.setRole(UserRole.ROLE_PREMIUM_USER);
        userRepository.save((user));

        try {
            auth0ManagementService.updateAuth0RoleToPremium(user.getAuth0Id());
        } catch (Exception e) {
           log.error("Failed to assign premium role for user in Auth0 : {}", user.getId(), e);
        }
    }

    @Override
    public void downgradeUserToFree(Long userId) {

        log.info("downgradeUserToFree called");
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with Id : "+userId));
        user.setSubscribed(false);
        user.setRole(UserRole.ROLE_FREE_USER);
        userRepository.save(user);

        try {
            auth0ManagementService.updateAuth0RoleToFree(user.getAuth0Id());
        } catch (Exception e) {
            log.error("Failed to assign free role for user in Auth0 : {}", user.getId(), e);
        }
    }

    @Override
    public User getUser(Long userId) {
        if(userId == null || userId <= 0) throw new IllegalArgumentException("User Id cannot be invalid");
        return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with Id : "+userId));
    }

}
