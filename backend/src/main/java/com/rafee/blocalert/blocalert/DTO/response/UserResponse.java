package com.rafee.blocalert.blocalert.DTO.response;

import com.rafee.blocalert.blocalert.entity.enums.Theme;
import com.rafee.blocalert.blocalert.entity.enums.UserRole;

public record UserResponse(
        String name,
        String email,
        String phoneNumber,
        UserRole role,
        Theme themePreference
) {
}
