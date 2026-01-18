package com.geo.bridge.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ExceptionCode {

    PARAMETER_INVALID("PI400", 400, "파라미터가 잘못되었습니다."),

    REFRESH_JWT_EXPIRED("J401", 400, "리프레쉬 만료"),
	INVALID_JWT_SIGN("J402", 400, "잘못된 JWT 서명"),
	EXPRIED_JWT("J403", 400, "만료된 JWT"),
	UNSUPPORTED_JWT("J404", 400, "지원하지 않는 JWT"),
	ERROR_JWT("J405", 400, "잘못된 JWT"),
	NOT_HEADER_JWT("J406", 400, "JWT 토큰이 헤더에 담겨있지 않음"),

    NOT_AUTHENTICATION_USER("P001", 400, "인증실패"),
	NOT_AUTHORIZED_USER("P002", 401, "인가되지 않은 사용자"),
    
    SERVER_INVALID("SI500", 500, "서버에러입니다."),
    ;

    private final String code;
    private final int status;
    private final String message;

}
