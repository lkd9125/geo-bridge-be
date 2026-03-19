package com.geo.bridge.api.user.host.model;


import com.geo.bridge.domain.user.host.dto.SearchHostDTO;

import lombok.Data;

/**
 * Emitter HOST 조회 RQ Web Model
 * 
 * <p>변수</p>
 * <ul>
 *     <li>{@link #idx} : HOST 식별값</li>
 * </ul>
 */
@Data
public class SearchUserHostRQ {

    private Long idx;

    public SearchHostDTO toDto(){ 
        return SearchHostDTO.builder()
            .idx(idx)
            .build();
    }

}
