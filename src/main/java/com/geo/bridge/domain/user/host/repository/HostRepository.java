package com.geo.bridge.domain.user.host.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;

import com.geo.bridge.domain.user.host.dto.entity.HostDTO;

/**
 * HOST 테이블 DB 조작 Repopsitory
 *
 * <p>기능</p>
 * <ul>
 *  <li>{@link R2dbcRepository} 기반 기본 CRUD</li>
 *  <li>{@link HostBaseRepository} 커스텀 검색/집계</li>
 * </ul>
 */
public interface HostRepository extends R2dbcRepository<HostDTO, Long>, HostBaseRepository{



}
