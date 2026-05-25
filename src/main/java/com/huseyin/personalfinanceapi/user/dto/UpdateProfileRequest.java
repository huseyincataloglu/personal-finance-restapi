package com.huseyin.personalfinanceapi.user.dto;

public record UpdateProfileRequest(
        String firstName,
        String lastName,
        String phoneNumber
) {


}
