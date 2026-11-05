package com.geo.bridge.api.user.info.model;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.geo.bridge.domain.user.dto.entity.User;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateUserRQ {

    @NotNull
    private String username;

    @NotNull
    private String password;

    public User toDto(PasswordEncoder encoder){
        LocalDateTime now = LocalDateTime.now();
        return User.builder()
            .username(username)
            .password(encoder.encode(password))
            .createAt(username)
            .createDt(now)
            .updateAt(username)
            .updateDt(now)
            .build();
    }
}
