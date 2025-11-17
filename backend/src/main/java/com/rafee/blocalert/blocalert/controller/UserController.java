package com.rafee.blocalert.blocalert.controller;

import com.auth0.exception.Auth0Exception;
import com.rafee.blocalert.blocalert.DTO.NewUserRequest;
import com.rafee.blocalert.blocalert.DTO.request.UserRequest;
import com.rafee.blocalert.blocalert.DTO.response.ApiResponse;
import com.rafee.blocalert.blocalert.DTO.response.UserResponse;
import com.rafee.blocalert.blocalert.service.AuthenticationService;
import com.rafee.blocalert.blocalert.service.UserService;
import com.rafee.blocalert.blocalert.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    @Value("${auth0.webhook.secret}")
    public String auth0WebhookSecret;

    private final AuthenticationService authenticationService;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Void> addNewUser(@RequestHeader("X-WEBHOOK-SECRET") String secret, @RequestBody NewUserRequest userRequest) {

        if (!Utils.validateAuth0Request(auth0WebhookSecret, secret)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        userService.createUser(userRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse> getUserDetails() {

        Long userId = authenticationService.getCurrentUserId();
        UserResponse user = userService.getUserDetails(userId);
        return ResponseEntity.ok().body(new ApiResponse("User fetched", user));
    }

    @PatchMapping
    public ResponseEntity<Void> updateUser(@RequestBody UserRequest request) {
        Long userId = authenticationService.getCurrentUserId();
        userService.updateUser(userId, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword() throws Auth0Exception {
        Long userId = authenticationService.getCurrentUserId();
        userService.resetPassword(userId);
        return ResponseEntity.ok().build();
    }
}
