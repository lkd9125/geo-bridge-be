package com.geo.bridge.api.user.format.model;

import java.time.LocalDateTime;

import com.geo.bridge.domain.user.format.dto.entity.Format;
import com.geo.bridge.global.security.SecurityHelper;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import reactor.core.publisher.Mono;

/**
 * <p>변수</p>
 * 
 * <ul>
 *  <li>{@link #name} 포맷팅 이름(별칭)</li>
 *  <li>{@link #format} 사용할 포맷</li>
 *  <li>{@link #contentType} 컨텐트 타입(nullable)</li>
 * </ul>
 */
@Data
public class CreateFormatRQ {

    @NotNull
    private String name;

    @NotNull
    private String format;

    private String contentType;

    public Mono<Format> toDto(){
        LocalDateTime now = LocalDateTime.now();
        return SecurityHelper.securityHolder()
            .map(userDetails -> Format.builder()
                    .name(name)
                    .format(format)
                    .contentType(contentType)
                    .createDt(now)
                    .updateDt(now)
                    .createAt(userDetails.getUsername())
                    .updateAt(userDetails.getUsername())
                    .build()
                );
    }
    
}
