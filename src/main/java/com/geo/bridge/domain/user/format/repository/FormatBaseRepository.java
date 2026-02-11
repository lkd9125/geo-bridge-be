package com.geo.bridge.domain.user.format.repository;

import org.springframework.data.domain.Pageable;

import com.geo.bridge.domain.user.format.dto.SearchFormatDTO;
import com.geo.bridge.domain.user.format.dto.entity.FormatDTO;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FormatBaseRepository{

    /**
     * 검색조건에 맞춘 Format 데이터 조회
     * @param searchDTO 검색 객체
     * @param pageable 페이지네이션
     * @return
     */
    Flux<FormatDTO> findBySearch(SearchFormatDTO searchDTO, Pageable pageable);

    /**
     * 검색조건에 맞춘 Format 데이터 총 개수 조회
     * @param searchDTO 검색 객체
     * @return
     */
    Mono<Long>  countBySearch(SearchFormatDTO searchDTO);
    
}
