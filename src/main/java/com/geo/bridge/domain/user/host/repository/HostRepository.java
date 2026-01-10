package com.geo.bridge.domain.user.host.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.domain.Pageable;

import com.geo.bridge.domain.user.host.dto.entity.HostDTO;
import com.geo.bridge.domain.user.host.dto.SearchHostDTO;

/**
 * EmitterClient 테이블 DB 조작 Repopsitory
 * 
 * <ul>
 *  <li>{@link #findBySearch(SearchHostDTO, Pageable) 검색조건에 맞춘 Emitter Client 데이터 조회} <</li>
 *  <li>{@link #countBySearch(SearchHostDTO) 검색조건에 맞춘 Emitter Client 데이터 총 개수 조회} <</li>
 * </ul>
 */
public interface HostRepository extends R2dbcRepository<HostDTO, Long>, HostBaseRepository{



}
