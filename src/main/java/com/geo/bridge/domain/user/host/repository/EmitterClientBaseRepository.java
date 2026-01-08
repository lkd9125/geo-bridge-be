package com.geo.bridge.domain.user.host.repository;


import org.springframework.data.domain.Pageable;

import com.geo.bridge.domain.user.host.dto.SearchEmitterDTO;
import com.geo.bridge.domain.user.host.dto.entity.ClientHostDTO;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EmitterClientBaseRepository {

    /**
     * 검색조건에 맞춘 Emitter Client 데이터 조회
     * @param searchDTO 검색 객체
     * @param pageable 페이지네이션
     * @return
     */
    Flux<ClientHostDTO> findBySearch(SearchEmitterDTO searchDTO, Pageable pageable);

    /**
     * 검색조건에 맞춘 Emitter Client 데이터 총 개수 조회
     * @param searchDTO 검색 객체
     * @return
     */
    Mono<Long> countBySearch(SearchEmitterDTO searchDTO);



}
