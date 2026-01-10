package com.geo.bridge.domain.user.host.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Host 검색용 DTO
 * 
 * <p>변수</p>
 * <ul>
 *  <li>{@link #idx EmitterClient PK 고유키}</li>
 * </ul>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchHostDTO {

    private Long idx;
    
}
