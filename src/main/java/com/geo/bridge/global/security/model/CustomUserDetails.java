package com.geo.bridge.global.security.model;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.geo.bridge.domain.user.dto.entity.UserDTO;

import lombok.Data;

@Data
public class CustomUserDetails implements UserDetails{

    private Long idx;

    private String username;

    private String password;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority("BASIC"));
    }

    public static UserDetails fromDto(UserDTO user){
        CustomUserDetails userDetails = new CustomUserDetails();
        userDetails.setIdx(user.getIdx());
        userDetails.setUsername(user.getUsername());
        userDetails.setPassword(user.getPassword());

        return userDetails;
    }

}
