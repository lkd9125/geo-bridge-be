package com.geo.bridge.global.security.handler;

import java.nio.charset.StandardCharsets;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.web.server.ServerWebExchange;

import com.geo.bridge.global.exception.ExceptionCode;
import com.geo.bridge.global.exception.model.BaseExceptionRS;
import com.geo.bridge.global.utils.JsonUtils;

import reactor.core.publisher.Mono;

public class AuthenticationWebLoginFailureHandler implements ServerAuthenticationFailureHandler{

    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException exception) {
        ServerWebExchange exchange = webFilterExchange.getExchange();

        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().set(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");

        ExceptionCode code = ExceptionCode.LOGIN_FAIL;

        BaseExceptionRS exceptionRS = new BaseExceptionRS();
        exceptionRS.setCode(code.getCode());
        exceptionRS.setDescription(exception.getMessage());
        exceptionRS.setMessage(code.getMessage());

        String jsonBody = JsonUtils.toJson(exceptionRS);

        DataBuffer buffer = response.bufferFactory().wrap(jsonBody.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
    }

}
