package com.rafee.blocalert.blocalert.DTO.request;

import com.rafee.blocalert.blocalert.entity.enums.Theme;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserRequest {

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits")
    private String phoneNumber;

    private Theme theme;

}
