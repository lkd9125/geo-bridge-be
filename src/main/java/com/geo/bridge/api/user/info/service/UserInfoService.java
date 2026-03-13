package com.geo.bridge.api.user.info.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.geo.bridge.api.user.info.model.CreateUserRQ;
import com.geo.bridge.api.user.info.model.GetUserRS;
import com.geo.bridge.domain.user.UserService;
import com.geo.bridge.global.exception.BaseException;
import com.geo.bridge.global.exception.ExceptionCode;
import com.geo.bridge.global.security.SecurityHelper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserInfoService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 유저 생성
     * @param mono
     * @return
     */
    public Mono<Void> createUser(Mono<CreateUserRQ> mono){
        return mono.flatMap(rq -> 
                userService.getUserByUsername(rq.getUsername())
                    // 1. 유저가 존재하면(중복이면) 에러 발생
                    .flatMap(existingUser -> {
                        return Mono.<CreateUserRQ>error(new BaseException(ExceptionCode.DUPLICATION_INVALID, "아이디 중복 "));
                    })
                    // 2. 유저가 없으면(Empty Mono면) 아래 로직 실행
                    .switchIfEmpty(Mono.just(rq)) 
            )
            .map(user -> user.toDto(passwordEncoder)) // 1. 모델변환
            .flatMap(user -> userService.createUser(Mono.just(user))); // 2. db save
    }

    /**
     * 유저 정보 조회
     * @return 유저 데이터 반환
     */
    public Mono<GetUserRS> getUser() {
        return SecurityHelper.securityHolder()
            .map(userDetails -> {
                GetUserRS user = new GetUserRS();
                return user;
            });
    }

    

}
