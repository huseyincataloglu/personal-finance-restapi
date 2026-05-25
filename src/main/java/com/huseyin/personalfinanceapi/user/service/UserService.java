package com.huseyin.personalfinanceapi.user.service;


import com.huseyin.personalfinanceapi.auth.exception.UserAccountDisabledException;
import com.huseyin.personalfinanceapi.common.accessgate.UserAccessGate;
import com.huseyin.personalfinanceapi.security.jwt.JwtService;
import com.huseyin.personalfinanceapi.security.jwt.RefreshTokenService;
import com.huseyin.personalfinanceapi.security.model.CustomUserDetails;
import com.huseyin.personalfinanceapi.user.dto.*;
import com.huseyin.personalfinanceapi.user.entity.User;
import com.huseyin.personalfinanceapi.user.event.UserCurrencySetEvent;
import com.huseyin.personalfinanceapi.user.exception.ChangePasswordException;
import com.huseyin.personalfinanceapi.user.exception.EmailChangeException;
import com.huseyin.personalfinanceapi.user.exception.PasswordMismatchException;
import com.huseyin.personalfinanceapi.user.exception.ValidTokenUserNotExistException;
import com.huseyin.personalfinanceapi.user.repository.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;

@Service
public class UserService {
    private UserAccessGate userAccessGate;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private RefreshTokenService refreshTokenService;
    private ApplicationEventPublisher eventPublisher;

    public UserService(
            UserAccessGate userAccessGate,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            RefreshTokenService refreshTokenService,
            ApplicationEventPublisher eventPublisher) {
        this.userAccessGate = userAccessGate;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.eventPublisher = eventPublisher;
    }

    public UserResponse get(Long id){
        User user =  userAccessGate.requireExistence(id);

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.isEnabled(),
                user.getRoles().stream().map(it -> it.getName().name()).toList(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );

    }


    @Transactional
    public UpdateProfileResponse  updateProfile(
            UpdateProfileRequest updateProfileRequest,
            Long userId
    ){
        User user = userAccessGate.requireActive(userId);

        if(updateProfileRequest.firstName() != null){
            user.setFirstName(updateProfileRequest.firstName());
        }
        if(updateProfileRequest.lastName() != null){
            user.setLastName(updateProfileRequest.lastName());
        }
        if(updateProfileRequest.phoneNumber() != null){
            user.setPhoneNumber(updateProfileRequest.phoneNumber());
        }

        userRepository.save(user);
        return  new UpdateProfileResponse(
                user.getId(),
                user.getEmail(),
                user.isEnabled(),
                user.getRoles().stream().map(it -> it.getName().name()).toList(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );

    }


    @Transactional
    public ChangePasswordRespone changePassword(
            String currentPassword,
            String newPassword,
            Long userId
    ) throws NoSuchAlgorithmException {

        User user =  userAccessGate.requireActive(userId);
        //Check if currentPassword in request maches existing hashed-one in Database
        if(!passwordEncoder.matches(currentPassword,user.getPassword())){
            throw new PasswordMismatchException("Current password does not match the existing one");
        }
        if(currentPassword.equals(newPassword)){
            throw  new ChangePasswordException("New password must be different from current one");
        }

        // Hashing new password and update in database
        String hashedNewPassword = passwordEncoder.encode(newPassword);
        user.setPassword(hashedNewPassword);
        userRepository.save(user);

        // We should revoke all existing refresh token because the password has been changed
        refreshTokenService.revokeAllForUser(user.getId());

        return new ChangePasswordRespone("Password is changed successfully.Please login again with new password");

    }

    @Transactional
    public ChangeEmailReponse changeEmail(
            String newEmail,
            String password,
            Long userId
    ){
        User user =  userAccessGate.requireActive(userId);
        //Check if currentPassword in request maches existing hashed-one in Database
        if(!passwordEncoder.matches(password,user.getPassword())){
            throw new PasswordMismatchException("Password mismatches.Email cannot be changed.");
        }
        if(user.getEmail().equals(newEmail)){
            throw new EmailChangeException("New email must be different from previous one");
        }

        user.setEmail(newEmail);
        userRepository.save(user);

        // We should revoke all existing refresh token because the email has been changed
        //User must log in again
        refreshTokenService.revokeAllForUser(user.getId());

        return new ChangeEmailReponse(
                "Email is changed successfully. Please login again with the new email"
        );

    }

    @Transactional
    public CompleteOnBoardingResponse completeOnboarding(
            Long userId,
            CompleteOnBoardingRequest request
    ){
        User user =  userAccessGate.requireActive(userId);
        if(user.isOnBoardingCompleted()){
            throw new IllegalStateException(
                    "Onboarding already completed"
            );
        }
        user.setBaseCurrency(request.baseCurrency());
        user.setOnBoardingCompleted(true);
        User updated = userRepository.save(user);

        eventPublisher.publishEvent(new UserCurrencySetEvent(user));

        return new CompleteOnBoardingResponse(updated.getBaseCurrency());
    }



    @Transactional
    public User changePreferences(Long userId,ChangePreferencesRequest request){
        User user =  userAccessGate.requireActive(userId);

        if(request.baseCurrency() != null){
            user.setBaseCurrency(request.baseCurrency());
        }
        return userRepository.save(user);

    }

    @Transactional
    public void disableUser(
            Long userId,
            DeactivateUserRequest request
    ){
        User user =  userAccessGate.requireExistence(userId);

        if(!user.isEnabled()){
            throw new UserAccountDisabledException("User's account already disabled and cannot be disabled again");
        }

        if(!passwordEncoder.matches(request.password(),user.getPassword())){
            throw new PasswordMismatchException("Password is invalid");
        }

        user.setEnabled(false);
        userRepository.save(user);

        refreshTokenService.revokeAllForUser(userId);

    }



}
