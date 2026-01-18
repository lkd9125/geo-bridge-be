package com.geo.bridge.global.security.handler;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.geo.bridge.global.exception.ExceptionCode;
import com.geo.bridge.global.exception.model.BaseExceptionRS;
import com.geo.bridge.global.utils.JsonUtils;

import reactor.core.publisher.Mono;

/**
 * 401 핸들러
 */
@Component
public class ReactiveAuthenticationEntryPoint implements ServerAuthenticationEntryPoint{

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ExceptionCode code = ExceptionCode.NOT_AUTHORIZED_USER;

        BaseExceptionRS exception = new BaseExceptionRS();
        exception.setCode(code.getCode());
        exception.setDescription(ex.getMessage());
        exception.setMessage(code.getMessage());

        String jsonBody = JsonUtils.toJson(exception);

        Mono<DataBuffer> dataBuffer = Mono.just(exchange.getResponse().bufferFactory().wrap(jsonBody.getBytes()));
        return exchange.getResponse()
            .writeWith(dataBuffer);
    }

}
