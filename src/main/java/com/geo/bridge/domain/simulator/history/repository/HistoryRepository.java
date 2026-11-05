package com.geo.bridge.domain.simulator.history.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;

import com.geo.bridge.domain.simulator.history.dto.entity.History;

import reactor.core.publisher.Mono;

public interface HistoryRepository extends R2dbcRepository<History, Long>, HistoryBaseRepository {

    /**
     * 사용자 소유의 실행 이력을 조회합니다.
     *
     * @param sn - 실행 이력 PK
     * @param username - 사용자 PK
     * @return 사용자 소유 실행 이력
     */
    Mono<History> findBySnAndUsername(Long sn, String username);

}
