package com.huseyin.personalfinanceapi.auth.dto;

public record RegisterRequestDto(
        String email,
        String password
) {
}
