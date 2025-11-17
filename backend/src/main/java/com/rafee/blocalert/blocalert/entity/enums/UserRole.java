package com.rafee.blocalert.blocalert.entity.enums;

public enum UserRole {

    ROLE_FREE_USER("rol_9VKBZVpLWSmzhl95"),
    ROLE_PREMIUM_USER("rol_yW8VAFauvk883tPp");

    private final String auth0RoleId;

    UserRole(String auth0RoleId) {
        this.auth0RoleId = auth0RoleId;
    }
    public String getAuth0RoleId() {
        return auth0RoleId;
    }

    public static UserRole fromAuth0Id(String auth0Id) {
        for (UserRole role : values()) {
            if (role.auth0RoleId.equals(auth0Id)) return role;
        }
        throw new IllegalArgumentException("Unknown Auth0 role ID: " + auth0Id);
    }
}
