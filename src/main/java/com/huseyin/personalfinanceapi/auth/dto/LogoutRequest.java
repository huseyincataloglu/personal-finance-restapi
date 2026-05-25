package com.huseyin.personalfinanceapi.auth.dto;

public record LogoutRequest(
        String refreshToken
) {
}
