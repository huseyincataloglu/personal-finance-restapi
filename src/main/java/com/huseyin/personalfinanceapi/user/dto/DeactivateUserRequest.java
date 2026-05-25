package com.huseyin.personalfinanceapi.user.dto;

import jakarta.validation.constraints.NotBlank;

public record DeactivateUserRequest(
        @NotBlank
        String password
) {}