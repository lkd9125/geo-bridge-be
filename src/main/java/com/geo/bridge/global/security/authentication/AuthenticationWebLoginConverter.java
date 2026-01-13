package com.geo.bridge.global.security.authentication;

import java.io.InputStream;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;

import com.geo.bridge.global.security.model.LoginRQ;
import com.geo.bridge.global.utils.JsonUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class AuthenticationWebLoginConverter implements ServerAuthenticationConverter{

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        // 비동기로 Body를 읽어서 LoginDto로 변환 후 Authentication 객체 리턴
        return exchange.getRequest().getBody()
            .next() // 첫 번째 데이터 버퍼를 가져옴 (데이터가 쪼개져 올 경우 조립 필요할 수 있음)
            .flatMap(buffer -> {
                try {
                    InputStream stream = buffer.asInputStream();
                    LoginRQ loginDto = JsonUtils.getInstance().readValue(stream, LoginRQ.class);
                    // 아직 인증되지 않은 토큰 생성
                    return Mono.just(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
                } catch (Exception e) {
                    log.error("ERROR :: ", e);
                    return Mono.error(e);
                }
            });
    }

}
