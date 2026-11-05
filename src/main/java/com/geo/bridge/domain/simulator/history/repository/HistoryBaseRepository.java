package com.geo.bridge.domain.simulator.history.repository;

import org.springframework.data.domain.Pageable;

import com.geo.bridge.domain.simulator.history.dto.entity.History;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface HistoryBaseRepository {

    /**
     * 사용자별 실행 이력을 조회합니다.
     *
     * @param username - 사용자 PK
     * @param pageable - 페이지 정보
     * @return 실행 이력 목록
     */
    Flux<History> findByUsername(String username, Pageable pageable);

    /**
     * 사용자별 실행 이력 개수를 조회합니다.
     *
     * @param username - 사용자 PK
     * @return 실행 이력 개수
     */
    Mono<Long> countByUsername(String username);

}
