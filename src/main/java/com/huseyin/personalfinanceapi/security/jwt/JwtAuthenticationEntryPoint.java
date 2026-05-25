package com.huseyin.personalfinanceapi.security.jwt;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

/**
 * This class aims to inform the users that you tried to reach a secured endpoint without authentication
 * This class runs in case of missing token, not invalid token
 */
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        String jsonBody = new tools.jackson.databind.ObjectMapper().writeValueAsString(Map.of(
                "status",401,
                "timeStamp", new Date().toString(),
                "error", "Unauthorized",
                "message", "Invalid or missing Jwt token",
                "path", request.getRequestURI()
        ));
        response.getWriter().write(jsonBody);

    }
}
