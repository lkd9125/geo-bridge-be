package com.geo.bridge.global.exception;

import java.io.Serial;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 1596180227522456297L;

    private final ExceptionCode errorCode;

    private final Throwable cause;

    private final Object[] args;

    private final String message;

    public BaseException() {
        this(ExceptionCode.SERVER_INVALID, null, ExceptionCode.SERVER_INVALID.getMessage(), null, false, true);
    }

    public BaseException(ExceptionCode errorCode) {
        this(errorCode, null, errorCode.getMessage(), null, false, true);
    }

    public BaseException(ExceptionCode errorCode, Throwable cause) {
        this(errorCode, cause, errorCode.getMessage(), null, false, true);
    }

    public BaseException(ExceptionCode errorCode, String message) {
        this(errorCode, null, message, null, false, true);
    }

    public BaseException(ExceptionCode errorCode, String message, Throwable cause) {
        this(errorCode, cause, message, null, false, true);
    }

    public BaseException(ExceptionCode errorCode, String message, Object... args) {
        this(errorCode, null, message, args, false, true);
    }

    public BaseException(ExceptionCode errorCode, Throwable cause, Object... args) {
        this(errorCode, cause, errorCode.getMessage(), args, false, true);
    }

    public BaseException(ExceptionCode errorCode, String message, Throwable cause, Object... args) {
        this(errorCode, cause, message, args, false, true);
    }

    public BaseException(ExceptionCode errorCode, Throwable cause, String message, Object[] args, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorCode = errorCode;
        this.cause = cause;
        this.args = args;
        this.message = message;
    }

}
