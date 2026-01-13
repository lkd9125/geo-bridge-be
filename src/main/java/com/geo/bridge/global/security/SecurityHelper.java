package com.geo.bridge.global.security;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import reactor.core.publisher.Mono;

public class SecurityHelper {

    public static Mono<UserDetails> securityHolder(){
        return ReactiveSecurityContextHolder.getContext()
            .map(context -> (UserDetails) context.getAuthentication().getPrincipal());
    }

}
