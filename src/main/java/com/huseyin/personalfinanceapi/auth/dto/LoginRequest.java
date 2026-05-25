package com.huseyin.personalfinanceapi.auth.dto;

public record LoginRequest(
        String email,
        String password
) {
}
