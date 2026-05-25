package com.huseyin.personalfinanceapi.auth.controller;


import com.huseyin.personalfinanceapi.auth.dto.*;
import com.huseyin.personalfinanceapi.auth.service.AuthService;
import com.huseyin.personalfinanceapi.security.model.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerEndpoint(
            @RequestBody @Valid RegisterRequestDto registerRequestDto
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("location","/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(authService.register(registerRequestDto));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginEndpoint(
            @RequestBody @Valid LoginRequest loginRequestDto
    ) throws NoSuchAlgorithmException {

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(authService.login(loginRequestDto));
    }


    /**
     * Logs out from single user session
     * refrest token representing the logged-in session should be passed
     * refresh token is revoked and user is logged-out
     * @param request
     * @param userId
     * @return
     * @throws NoSuchAlgorithmException
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestBody LogoutRequest request,
            @AuthenticationPrincipal Long userId
    ) throws NoSuchAlgorithmException {

        authService.logout(userId, request.refreshToken());
        return ResponseEntity.noContent().build();
    }

    /**
     * Gets old refresh token,revokes it and issue a new refresh token
     * then generates a new access JWT token then returns them all
     * @param request
     * @return
     * @throws NoSuchAlgorithmException
     */
    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refresh(@RequestBody RefreshRequest request) throws NoSuchAlgorithmException {
        return ResponseEntity.ok(authService.refresh(request));
    }


}
