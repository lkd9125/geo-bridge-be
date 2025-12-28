package com.geo.bridge.domain.emitter.repository;


import org.springframework.data.domain.Pageable;

import com.geo.bridge.domain.emitter.dto.SearchEmitterDTO;
import com.geo.bridge.domain.emitter.dto.entity.EmitterClientDTO;
import com.geo.bridge.global.base.BasePageRS;

import reactor.core.publisher.Mono;

public interface EmitterClientBaseRepository {

    /**
     * 검색조건에 맞춘 Emitter Client 데이터 조회
     * @param searchDTO 검색 객체
     * @param pageable 페이지네이션
     * @return
     */
    Mono<BasePageRS<EmitterClientDTO>> findBySearch(SearchEmitterDTO searchDTO, Pageable pageable);

}
