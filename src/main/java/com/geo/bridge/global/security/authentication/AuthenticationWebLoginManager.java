package com.geo.bridge.global.security.authentication;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.geo.bridge.domain.user.UserService;
import com.geo.bridge.domain.user.dto.entity.UserDTO;
import com.geo.bridge.global.security.model.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class AuthenticationWebLoginManager implements ReactiveAuthenticationManager{
    
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        return userService.getUserByUsername(username) // 1. DB에 username조회
            .switchIfEmpty(Mono.defer(() -> {
                log.warn("USER NOT FOUND :: {}", username);
                return Mono.<UserDTO>error(new BadCredentialsException("로그인 실패"));
            }))
            .map(CustomUserDetails::fromDto) // 2. User Details로 변환
            .flatMap(userDetails -> {
                if (passwordEncoder.matches(password, userDetails.getPassword())) { // 3-1. 비밀번호 체크
                    return Mono.just(new UsernamePasswordAuthenticationToken(username, null, userDetails.getAuthorities()));
                } else { // 3-2. 체크 후 틀리면 exception 던지기
                    log.warn("USER PASSWORD IS NOT MATCH :: {}", password);
                    return Mono.error(new BadCredentialsException("로그인 실패"));
                }
            });
        }

}
