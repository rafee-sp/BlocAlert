package com.rafee.blocalert.blocalert.DTO;

import java.time.LocalDateTime;

public record NewUserRequest(
        String auth0Id,
        String email,
        String name,
        LocalDateTime createdAt
) {
}
