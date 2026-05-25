package com.huseyin.personalfinanceapi.security.model;


import com.huseyin.personalfinanceapi.user.entity.Roles;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;


@Setter
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    private Long id;
    private String email;
    private String password;
    private boolean isEnabled;
    private Set<Roles> roleSet;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roleSet.stream().map(it -> new SimpleGrantedAuthority("ROLE_"+it.getName().name())).toList();
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return email;
    }

    public Long getId(){
        return id;
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return isEnabled; }

}
