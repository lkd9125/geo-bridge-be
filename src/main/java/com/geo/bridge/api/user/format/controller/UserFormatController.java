package com.geo.bridge.api.user.format.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.geo.bridge.api.user.format.model.CreateFormatRQ;
import com.geo.bridge.api.user.format.service.UserFormatService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * 유저 포맷 API Controller
 * Validation 처리
 * 
 * <p>기능</p>
 * <ul>
 *  <li>{@link #createFormat(Mono)} 포맷 생성</li>
 * </ul>
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/user/format")
public class UserFormatController {

    private final UserFormatService userFormatService;

    @PostMapping
    public Mono<Void> createFormat(@RequestBody @Valid Mono<CreateFormatRQ> rq){
        return userFormatService.createForamt(rq);
    }

}
