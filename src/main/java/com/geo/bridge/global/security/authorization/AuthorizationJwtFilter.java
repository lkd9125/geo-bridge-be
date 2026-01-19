package com.geo.bridge.global.security.authorization;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.geo.bridge.global.exception.BaseException;
import com.geo.bridge.global.exception.ExceptionCode;
import com.geo.bridge.global.exception.model.BaseExceptionRS;
import com.geo.bridge.global.security.TokenProvider;
import com.geo.bridge.global.utils.JsonUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class AuthorizationJwtFilter implements WebFilter{

    private final TokenProvider tokenProvider;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String token = tokenProvider.getHeaderToken(exchange.getRequest());

        return Mono.just(token)
            .filter(jwt -> jwt != null && tokenProvider.validateToken(jwt)) // 1. validation 체크
            .map(jwt -> tokenProvider.getAuthentication(token)) // 2. 토큰을 (Security) authentication으로 변환
            .flatMap(auth -> { // 3. ReactiveSecurityContextHolder에 인가 데이터 put
                return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
            })
            .switchIfEmpty(chain.filter(exchange)) // 만약 token이 없는경우 로그인이 되지 않은 상태로 security flow 이어가도록 처리
            .onErrorResume(e -> { // exception 처리
                ExceptionCode code = ExceptionCode.ERROR_JWT;
                if(e instanceof BaseException baseException){
                    code = baseException.getErrorCode();
                } 

                BaseExceptionRS exceptionRS = new BaseExceptionRS();
                exceptionRS.setCode(code.getCode());
                exceptionRS.setDescription(e.getMessage());
                exceptionRS.setMessage(code.getMessage());

                String jsonBody = JsonUtils.toJson(exceptionRS);

                DataBuffer buffer = response.bufferFactory().wrap(jsonBody.getBytes());

                return response.writeWith(Mono.just(buffer));
            });
    }

}
