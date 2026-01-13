package com.geo.bridge.global.security.model;

import lombok.Data;

@Data
public class LoginRQ {

    // 아이디
    private String username;

    // 비밀번호
    private String password;

}
