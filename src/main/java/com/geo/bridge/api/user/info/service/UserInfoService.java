package com.geo.bridge.api.user.info.service;

import org.springframework.stereotype.Service;

import com.geo.bridge.api.user.info.model.CreateUserRQ;
import com.geo.bridge.domain.user.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserInfoService {

    private final UserService userService;

    /**
     * 유저 생성
     * @param mono
     * @return
     */
    public Mono<Void> createUser(Mono<CreateUserRQ> mono){
        return mono
            .map(CreateUserRQ::toDto) // 1. 모델변환
            .flatMap(user -> userService.createUser(Mono.just(user))); // 2. db save
    }

}
