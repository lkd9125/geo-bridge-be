package com.geo.bridge.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import com.geo.bridge.global.exception.model.BaseExceptionRS;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * SpringValidation의 Exception처리
     * @param throwable Spring Validation Exception throwable
     * @return
     */
    @ExceptionHandler(exception = WebExchangeBindException.class)
    public Mono<ResponseEntity<BaseExceptionRS>> handleValdationException(Exception throwable){
        ExceptionCode code = ExceptionCode.PARAMETER_INVALID;
        BaseExceptionRS model  = new BaseExceptionRS();
        model.setCode(code.getCode());
        model.setMessage(code.getMessage());

        if(throwable instanceof WebExchangeBindException e){
            BindingResult bindingResult = e.getBindingResult();
            StringBuilder sb = new StringBuilder();
            for(FieldError fieldError : bindingResult.getFieldErrors()){
                String field = fieldError.getField();
                String message = fieldError.getDefaultMessage();
                sb
                    .append("[(")
                    .append(field)
                    .append(") ")
                    .append(message)
                    .append("]")
                    .append(",");
            }

            sb.deleteCharAt(sb.length() - 1);
            model.setDescription(sb.toString());
        } else {
            model.setDescription(throwable.getMessage());
        }
        
        log.warn("validation exception :: {}", model.getDescription());

        return Mono.just(
            ResponseEntity.status(code.getStatus())
                .body(model)
        );
    }

    /**
     * 예상치 못한 모든 예외 처리
     * @param e exception
     * @return
     */
    @ExceptionHandler(BaseException.class)
    public Mono<ResponseEntity<BaseExceptionRS>> handleAllException(BaseException e) {
        log.error("Exception Class :: {}, Mesaage :: {}", e.getClass(), e.getMessage());
        log.error("Unhandled Exception: ", e);
        
        ExceptionCode code = e.getErrorCode();
        BaseExceptionRS model = new BaseExceptionRS();
        model.setCode(code.getCode());
        model.setMessage(code.getMessage());

        return Mono.just(
            ResponseEntity.status(code.getStatus())
                .body(model)
        );
    }

    /**
     * 예상치 못한 모든 예외 처리
     * @param e exception
     * @return
     */
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<BaseExceptionRS>> handleAllException(Exception e) {
        log.error("Exception Class :: {}, Mesaage :: {}", e.getClass(), e.getMessage());
        log.error("Unhandled Exception: ", e);
        
        ExceptionCode code = ExceptionCode.SERVER_INVALID;
        BaseExceptionRS model = new BaseExceptionRS();
        model.setCode(code.getCode());
        model.setMessage(code.getMessage());

        return Mono.just(
            ResponseEntity.status(code.getStatus())
                .body(model)
        );
    }

}
