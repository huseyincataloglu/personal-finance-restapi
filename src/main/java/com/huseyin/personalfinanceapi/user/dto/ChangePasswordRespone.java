package com.huseyin.personalfinanceapi.user.dto;

import com.huseyin.personalfinanceapi.security.jwt.JwtService;
import com.huseyin.personalfinanceapi.security.jwt.RefreshTokenService;

import java.util.Map;

public record ChangePasswordRespone(
        String message
) {
}
