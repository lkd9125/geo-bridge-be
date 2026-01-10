package com.geo.bridge.domain.user.format.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;

import com.geo.bridge.domain.user.format.dto.entity.FormatDTO;

/**
 * FORMAT 테이블 DB 조작 Repopsitory
 * 
 * <ul>
 *  <li></li>
 * </ul>
 */
public interface FormatRepository extends R2dbcRepository<FormatDTO, Long>{
    
}
