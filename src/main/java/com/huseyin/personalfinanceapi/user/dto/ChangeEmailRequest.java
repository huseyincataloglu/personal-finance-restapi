package com.huseyin.personalfinanceapi.user.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangeEmailRequest(
        @NotBlank String newEmail,
        @NotBlank String password
) {
}
