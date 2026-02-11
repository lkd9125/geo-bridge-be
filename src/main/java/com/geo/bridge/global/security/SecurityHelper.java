package com.geo.bridge.global.security;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;

import com.geo.bridge.global.security.model.CustomUserDetails;

import reactor.core.publisher.Mono;

public class SecurityHelper {

    public static Mono<CustomUserDetails> securityHolder(){
        return ReactiveSecurityContextHolder.getContext()
            .map(context -> (CustomUserDetails) context.getAuthentication().getPrincipal());
    }

}
