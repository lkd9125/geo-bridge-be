package com.geo.bridge.api.user.info.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.geo.bridge.api.user.info.model.CreateUserRQ;
import com.geo.bridge.api.user.info.model.GetUserRS;
import com.geo.bridge.api.user.info.service.UserInfoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/user/info")
public class UserInfoController {

    private final UserInfoService userInfoService;

    /**
     * 유저 생성
     * @param mono {@link CreateUserRQ} 유저 생성 요청
     * @return 생성 완료 시그널
     */
    @PostMapping
    public Mono<Void> createUser(@RequestBody @Valid Mono<CreateUserRQ> mono){
        return userInfoService.createUser(mono);
    }

    /**
     * 유저 정보 반환
     * @return 인증 사용자 정보
     */
    @GetMapping
    public Mono<GetUserRS> getUser(){
        return userInfoService.getUser();
    }
}
