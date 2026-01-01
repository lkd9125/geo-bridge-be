package com.geo.bridge.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ExceptionCode {

    PARAMETER_INVALID("PI400", 400, "파라미터가 잘못되었습니다."),
    
    SERVER_INVALID("SI500", 500, "서버에러입니다."),
    ;

    private final String code;
    private final int status;
    private final String message;

}
