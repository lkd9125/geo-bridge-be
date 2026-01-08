package com.geo.bridge.domain.user.host.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;

import com.geo.bridge.domain.user.host.dto.entity.ClientHostDTO;

/**
 * EmitterClient 테이블 DB 조작 Repopsitory
 * 
 * <ul>
 *  <li>{@link #findBySearch(com.geo.bridge.domain.emitter.dto.SearchEmitterDTO, org.springframework.data.domain.Pageable) 검색조건에 맞춘 Emitter Client 데이터 조회} <</li>
 * </ul>
 */
public interface EmitterClientRepository extends R2dbcRepository<ClientHostDTO, Long>, EmitterClientBaseRepository{



}
