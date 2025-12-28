package com.geo.bridge.domain.emitter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Emitter 검색용 DTO
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
public class SearchEmitterDTO {

    private Long idx;
    
}
