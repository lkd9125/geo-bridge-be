package com.geo.bridge.global.security.handler;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import java.nio.charset.StandardCharsets;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.geo.bridge.global.exception.ExceptionCode;
import com.geo.bridge.global.exception.model.BaseExceptionRS;
import com.geo.bridge.global.utils.JsonUtils;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * 401 핸들러
 */
@Component
@Slf4j
public class ReactiveAuthenticationEntryPoint implements ServerAuthenticationEntryPoint{

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        log.error("ERROR : ", ex.getMessage());
        
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        // 응답 Content-Type에 UTF-8 charset을 명시
        exchange.getResponse().getHeaders().set(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");

        ExceptionCode code = ExceptionCode.NOT_AUTHORIZED_USER;

        BaseExceptionRS exception = new BaseExceptionRS();
        exception.setCode(code.getCode());
        exception.setDescription(ex.getMessage());
        exception.setMessage(code.getMessage());

        String jsonBody = JsonUtils.toJson(exception);

        Mono<DataBuffer> dataBuffer = Mono.just(exchange.getResponse().bufferFactory().wrap(jsonBody.getBytes(StandardCharsets.UTF_8)));
        return exchange.getResponse()
            .writeWith(dataBuffer);
    }

}
