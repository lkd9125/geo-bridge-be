package com.geo.bridge.api.user.host.model;


import com.geo.bridge.domain.user.host.dto.SearchEmitterDTO;

import lombok.Data;

/**
 * Emitter Client 조회 RQ Web Model
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
public class SearchUserHostRQ {

    private Long idx;

    public SearchEmitterDTO toDto(){ 
        return SearchEmitterDTO.builder()
            .idx(idx)
            .build();
    }

}
