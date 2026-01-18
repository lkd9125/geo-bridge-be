package com.geo.bridge.api.user.info.model;

import java.time.LocalDateTime;

import com.geo.bridge.domain.user.dto.entity.UserDTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateUserRQ {

    @NotNull
    private String username;

    @NotNull
    private String password;

    public UserDTO toDto(){
        LocalDateTime now = LocalDateTime.now();
        return UserDTO.builder()
            .username(username)
            .password(password)
            .createAt(username)
            .createDt(now)
            .updateAt(username)
            .updateDt(now)
            .build();
    }
}
