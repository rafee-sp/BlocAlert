package com.rafee.blocalert.blocalert.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Theme {

    LIGHT("light"),
    DARK("dark");

    private final String value;

    Theme(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return  value;
    }

    @JsonCreator
    public static Theme fromValue(String value) {
        for (Theme theme : Theme.values()) {
            if (theme.value.equalsIgnoreCase(value)) {
                return theme;
            }
        }
        throw new IllegalArgumentException("Invalid theme: " + value);
    }
}
