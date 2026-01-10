package com.geo.bridge.api.user.host.model;

import java.time.LocalDateTime;

import com.geo.bridge.domain.emitter.integration.model.EmitterType;
import com.geo.bridge.domain.user.host.dto.entity.HostDTO;
import com.geo.bridge.global.security.SecurityHelper;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Emitter Client 저장 RQ Web Model
 * 
 * <p>변수</p>
 * <ul>
 *     <li>{@link #name} : 클라이어트 식별 이름</li>
 *     <li>{@link #host} : 연결 호스트</li>
 *     <li>{@link #type} : 연결 타입</li>
 *     <li>{@link #topic} : 토픽</li>
 *     <li>{@link #hostId} : 아이디</li>
 *     <li>{@link #password} : 비밀번호</li>
 * </ul>
 */
@Data
public class CreateUserHostRQ {

    @NotNull
    private EmitterType type;

    @NotNull
    private String host;

    @NotNull
    private String name;

    private String topic;

    private String hostId;

    private String password;

    public HostDTO toDto(){
        LocalDateTime now = LocalDateTime.now();
        return HostDTO.builder()
            .name(name)
            .host(host)
            .type(type)
            .topic(topic)
            .hostId(hostId)
            .password(password)
            .createDt(now)
            .updateDt(now)
            .createAt(SecurityHelper.securityHolder().toString())
            .updateAt(SecurityHelper.securityHolder().toString())
            .build();
    }

}
