package com.geo.bridge.global.security.authentication;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.geo.bridge.domain.user.repository.UserRepository;
import com.geo.bridge.global.security.model.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class AuthenticationWebLoginManager implements ReactiveAuthenticationManager{
    
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        return userRepository.findByUsername(username)
            .map(CustomUserDetails::fromDto) // 1. User Details로 변환
            .filter(userDetails -> passwordEncoder.matches(password, userDetails.getPassword())) // 2. password가 맞는지 체크
            .map(userDetails -> new UsernamePasswordAuthenticationToken(username, password, userDetails.getAuthorities())); //  3. Authentication 변환
        }

}
