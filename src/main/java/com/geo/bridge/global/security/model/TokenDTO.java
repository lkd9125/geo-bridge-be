package com.geo.bridge.global.security.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenDTO {

    private String grantType;

    private String accessToken;

    private String refreshToken;

}
