package com.huseyin.personalfinanceapi.user.controller;



import com.huseyin.personalfinanceapi.user.dto.*;
import com.huseyin.personalfinanceapi.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<?> get(
            @AuthenticationPrincipal Long userId
    ){

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.get(userId));

    }

    @PatchMapping("/me")
    public ResponseEntity<?> updateProfile(
            @RequestBody UpdateProfileRequest updateProfieRequest,
            @AuthenticationPrincipal Long userId
    ){
        return ResponseEntity
                .ok(userService.updateProfile(updateProfieRequest,userId));
    }


    @PatchMapping("/me/email")
    public ResponseEntity<?> changeEmail(
            @RequestBody ChangeEmailRequest changeEmailRequest,
            @AuthenticationPrincipal Long userId
    ){
        return ResponseEntity.status(HttpStatus.OK)
                        .body(userService.changeEmail(changeEmailRequest.newEmail(),changeEmailRequest.password(),userId));
    }

    @PatchMapping("/me/password")
    public ResponseEntity<?> changePassword(
           @RequestBody @Valid ChangePasswordRequest changePasswordRequest,
            @AuthenticationPrincipal Long userId
    ) throws NoSuchAlgorithmException {

        ChangePasswordRespone changePasswordRespone =userService.changePassword(
                changePasswordRequest.currentPassword(),
                changePasswordRequest.newPassword(),
                userId
        );
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(changePasswordRespone);

    }

    @PostMapping("/me/onboarding")
    public ResponseEntity<CompleteOnBoardingResponse> completeOnboarding(
            @AuthenticationPrincipal Long userId,
            @RequestBody @Valid CompleteOnBoardingRequest request
    ) {

        return ResponseEntity.ok(
                userService.completeOnboarding(
                        userId,
                        request
                )
        );
    }

    @PatchMapping("/me/preferences")
    public ResponseEntity<?> changePassword(
            @RequestBody @Valid ChangePreferencesRequest changePreferencesRequest,
            @AuthenticationPrincipal Long userId
    ){
        return ResponseEntity
                .ok(userService.changePreferences(userId,changePreferencesRequest));
    }

    @PostMapping("/me/deactivate")
    public ResponseEntity<?> deleteUser(
            @AuthenticationPrincipal Long userId,
            @RequestBody @Valid DeactivateUserRequest deactivateUserRequest
    ){
        userService.disableUser(userId,deactivateUserRequest);

        return ResponseEntity.ok().build();

    }



}
