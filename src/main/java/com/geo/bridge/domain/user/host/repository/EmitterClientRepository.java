package com.geo.bridge.domain.user.host.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.domain.Pageable;

import com.geo.bridge.domain.user.host.dto.entity.ClientHostDTO;
import com.geo.bridge.domain.user.host.dto.SearchEmitterDTO;

/**
 * EmitterClient 테이블 DB 조작 Repopsitory
 * 
 * <ul>
 *  <li>{@link #findBySearch(SearchEmitterDTO, Pageable) 검색조건에 맞춘 Emitter Client 데이터 조회} <</li>
 *  <li>{@link #countBySearch(SearchEmitterDTO) 검색조건에 맞춘 Emitter Client 데이터 총 개수 조회} <</li>
 * </ul>
 */
public interface EmitterClientRepository extends R2dbcRepository<ClientHostDTO, Long>, EmitterClientBaseRepository{



}
