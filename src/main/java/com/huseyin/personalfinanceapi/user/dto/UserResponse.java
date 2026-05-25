package com.huseyin.personalfinanceapi.user.dto;

import java.time.LocalDateTime;
import java.util.List;

public record UserResponse(
        Long id,
        String username,
        boolean isEnabled,
        List<String> roles,
        String firstName,
        String lastName,
        String phoneNumber,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
