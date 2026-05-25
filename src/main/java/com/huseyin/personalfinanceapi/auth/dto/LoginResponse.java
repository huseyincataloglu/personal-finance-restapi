package com.huseyin.personalfinanceapi.auth.dto;

import com.huseyin.personalfinanceapi.security.jwt.JwtService;
import com.huseyin.personalfinanceapi.security.jwt.RefreshTokenService;

import java.util.Map;

public record LoginResponse(
        String message,
        JwtService.IssuedAccessToken accessToken,
        RefreshTokenService.IssuedToken refreshToken
) {

}
