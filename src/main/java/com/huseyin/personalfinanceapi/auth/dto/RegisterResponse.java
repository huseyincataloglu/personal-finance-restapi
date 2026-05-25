package com.huseyin.personalfinanceapi.auth.dto;

import com.huseyin.personalfinanceapi.user.entity.Roles;
import com.huseyin.personalfinanceapi.user.entity.User;

import java.util.Set;

public record RegisterResponse(
        Long id,
        String email,
        boolean isEnabled,
        Set<Roles> roles
) {
    public static RegisterResponse from(User user){
        return new RegisterResponse(user.getId(),user.getEmail(), user.isEnabled(),user.getRoles());
    }


}
