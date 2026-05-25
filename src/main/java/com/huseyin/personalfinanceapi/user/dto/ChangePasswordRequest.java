package com.huseyin.personalfinanceapi.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotBlank @Size(min = 6,max = 12) String currentPassword,
        @NotBlank @Size(min = 6,max = 12) String newPassword
) {
}
