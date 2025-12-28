package com.geo.bridge.domain.emitter.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;

import com.geo.bridge.domain.emitter.dto.entity.EmitterClientDTO;

/**
 * EmitterClient 테이블 DB 조작 Repopsitory
 * 
 * <ul>
 *  <li>{@link #findBySearch(com.geo.bridge.domain.emitter.dto.SearchEmitterDTO, org.springframework.data.domain.Pageable) 검색조건에 맞춘 Emitter Client 데이터 조회} <</li>
 * </ul>
 */
public interface EmitterClientRepository extends R2dbcRepository<EmitterClientDTO, Long>, EmitterClientBaseRepository{



}
