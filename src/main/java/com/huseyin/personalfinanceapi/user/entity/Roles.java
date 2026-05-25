package com.huseyin.personalfinanceapi.user.entity;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.huseyin.personalfinanceapi.user.exception.InvalidUserRoleException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;


@Entity
@Table(name = "user_roles")
@Getter
@Setter
@NoArgsConstructor
public class Roles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @Enumerated(EnumType.STRING)
    private UserRole name;

    public enum UserRole {
        USER,
        ADMIN;

        @JsonCreator
        public static UserRole from(String userRole) {
            return Arrays.stream(UserRole.values()).filter(it -> it.name().equals(userRole)).findFirst()
                    .orElseThrow(() -> new InvalidUserRoleException(userRole +"is invalid:" ));
        }


    }
}
