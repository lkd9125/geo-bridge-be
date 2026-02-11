package com.geo.bridge.global.security.authorization;

import java.nio.charset.StandardCharsets;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.geo.bridge.domain.user.repository.UserRepository;
import com.geo.bridge.global.exception.BaseException;
import com.geo.bridge.global.exception.ExceptionCode;
import com.geo.bridge.global.exception.model.BaseExceptionRS;
import com.geo.bridge.global.security.TokenProvider;
import com.geo.bridge.global.security.model.CustomUserDetails;
import com.geo.bridge.global.utils.JsonUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class AuthorizationJwtFilter implements WebFilter{

    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String token = tokenProvider.getHeaderToken(exchange.getRequest());

        try {
            if (token != null && tokenProvider.validateToken(token)) {
                Authentication auth = tokenProvider.getAuthentication(token);
                return userRepository.findById(auth.getName())
                    .map(user -> CustomUserDetails.fromDto(user))
                    .cast(CustomUserDetails.class)
                    .flatMap(customUserDetails -> 
                        chain.filter(exchange)
                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth))
                    );
            }
        } catch (Exception e) {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.OK);
            response.getHeaders().set(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");

            ExceptionCode code = ExceptionCode.ERROR_JWT;
            if (e instanceof BaseException baseException) {
                code = baseException.getErrorCode();
            }

            BaseExceptionRS exceptionRS = new BaseExceptionRS();
            exceptionRS.setCode(code.getCode());
            exceptionRS.setDescription(e.getMessage());
            exceptionRS.setMessage(code.getMessage());

            String jsonBody = JsonUtils.toJson(exceptionRS);
            DataBuffer buffer = response.bufferFactory().wrap(jsonBody.getBytes(StandardCharsets.UTF_8));

            return response.writeWith(Mono.just(buffer));
        }

        // 토큰이 없는 경우 로그인되지 않은 상태로 security flow 이어감
        return chain.filter(exchange);
    }

}
