package com.huseyin.personalfinanceapi.auth.service;


import com.huseyin.personalfinanceapi.auth.dto.*;
import com.huseyin.personalfinanceapi.auth.exception.UserAccountDisabledException;
import com.huseyin.personalfinanceapi.auth.exception.UserRegisterationException;
import com.huseyin.personalfinanceapi.security.jwt.JwtService;
import com.huseyin.personalfinanceapi.security.jwt.RefreshTokenService;
import com.huseyin.personalfinanceapi.user.entity.Roles;
import com.huseyin.personalfinanceapi.user.entity.User;
import com.huseyin.personalfinanceapi.user.repository.RolesRepository;
import com.huseyin.personalfinanceapi.user.repository.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private UserRepository userRepository;
    private RolesRepository rolesRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private RefreshTokenService refreshTokenService;


    public AuthService(
            UserRepository userRepository,
            RolesRepository rolesRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            RefreshTokenService refreshTokenService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.rolesRepository = rolesRepository;
    }


    /**
     * Registers new user to the system
     * Checks first ıf the email in request already exists then hash the password with an encoder
     * then save it
     * @param registerRequest
     * @return RegisterResponse
     */
    public RegisterResponse register(
            RegisterRequestDto registerRequest
    ){
        if(userRepository.existsByEmail(registerRequest.email())){
            throw new UserRegisterationException("There is already user with email:"+ registerRequest.email());
        }

        User newUser = new User();
        newUser.setEmail(registerRequest.email());
        newUser.setPassword(passwordEncoder.encode(registerRequest.password()));
        newUser.setEnabled(true);
        newUser.setOnBoardingCompleted(false);

        Roles role = rolesRepository.findByName(Roles.UserRole.from("USER"));
        Set<Roles> roles = new HashSet<>();
        roles.add(role);
        newUser.setRoles(roles);

        User saved = userRepository.save(newUser);
        return RegisterResponse.from(saved);

    }

    public LoginResponse login(
            LoginRequest loginRequest
    ) throws NoSuchAlgorithmException {
        User user =  userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new UsernameNotFoundException("Email or password is invalid"));

        if(!passwordEncoder.matches(loginRequest.password(),user.getPassword())){
            throw new UsernameNotFoundException("Email or password is invalid");
        }

        if(!user.isEnabled()){
            throw new UserAccountDisabledException("The user's account is disabled anc cannotl ogin");
        }

        JwtService.IssuedAccessToken accessToken = jwtService.createAccessToken(
                user.getId(),
                user.getEmail(),
                user.getRoles().stream().map(it -> it.getName().name()).toList());

        RefreshTokenService.IssuedToken refreshToken   = refreshTokenService.issue(user.getId());
        return new LoginResponse(
                "Login is successfull",
                accessToken,
                refreshToken
        );

    }

    public RefreshResponse refresh(
            RefreshRequest refreshRequest
    ) throws NoSuchAlgorithmException {
        //Perform rotation process
        RefreshTokenService.RotationResult rotationResult = refreshTokenService.rotate(refreshRequest.refreshToken());
        if(rotationResult.exception() != null){
            throw rotationResult.exception();
        }

        //If rotation is successfull, then generate a new access token
        User user = userRepository.findById(rotationResult.userId()).get();

        JwtService.IssuedAccessToken accessToken = jwtService.createAccessToken(
                rotationResult.userId(),
                user.getEmail(),
                user.getRoles().stream().map(it -> it.getName().name()).toList()
                );

        return new RefreshResponse(
                "Refresh is succeeded.New access and refresh tokens produced",
                accessToken,
                rotationResult.issuedToken()
        );


    }

    public void logout(Long userId, String rawRefreshToken) throws NoSuchAlgorithmException {
        refreshTokenService.revoke(userId, rawRefreshToken);
    }



}
