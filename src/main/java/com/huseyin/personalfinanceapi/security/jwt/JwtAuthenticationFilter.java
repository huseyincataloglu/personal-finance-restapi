package com.huseyin.personalfinanceapi.security.jwt;



import com.huseyin.personalfinanceapi.security.model.CustomUserDetails;
import com.huseyin.personalfinanceapi.security.service.CustomUserDetailsService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if(header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            String id;
            try {
                id = jwtService.validateAndExtractId(token);
                List<String> userRoles = (List<String>) jwtService.extractRoles(token);
                List<SimpleGrantedAuthority> authorities = userRoles.stream()
                        .map(it -> new SimpleGrantedAuthority("ROLE_"+it)).toList();
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    Authentication authentication =
                            new UsernamePasswordAuthenticationToken(
                                    Long.valueOf(id),
                                    null,
                                    authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (JwtException ex) {
                logger.warn("Invalid jwt token", ex);
                jwtExResponse(response, request);
                return;
            }
            catch (UsernameNotFoundException ex){
                logger.warn(ex.getMessage());
                userResponse(response, request);
                return;
            }
        }
        filterChain.doFilter(request,response);

    }

    private void jwtExResponse(HttpServletResponse httpServletResponse,HttpServletRequest request) throws IOException {
        httpServletResponse.setStatus(401);
        httpServletResponse.setContentType("application/json");
        String jsonBody = new ObjectMapper().writeValueAsString(
                Map.of(
                        "status",401,
                        "timeStamp", new Date().toString(),
                        "error", "Unauthorized",
                        "message", "Invalid or missing Jwt token",
                        "path", request.getRequestURI()
                )
        );
        httpServletResponse.getWriter().write(jsonBody);
    }
    private void userResponse(HttpServletResponse httpServletResponse,HttpServletRequest request) throws IOException {
        httpServletResponse.setStatus(401);
        httpServletResponse.setContentType("application/json");
        String jsonBody = new ObjectMapper().writeValueAsString(
                Map.of(
                        "status",401,
                        "timeStamp", new Date().toString(),
                        "error", "Unauthorized",
                        "message", "User in Jwt token is not identified in the system",
                        "path", request.getRequestURI()
                )
        );
        httpServletResponse.getWriter().write(jsonBody);
    }

}
