package com.geo.bridge.api.emitter.client.info.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.geo.bridge.api.emitter.client.info.model.CreateEmitterClientInfoRQ;
import com.geo.bridge.api.emitter.client.info.model.SearchEmitterClientInfoRQ;
import com.geo.bridge.api.emitter.client.info.model.SearchEmitterClientInfoRS;
import com.geo.bridge.domain.emitter.EmitterClientService;
import com.geo.bridge.domain.emitter.dto.SearchEmitterDTO;
import com.geo.bridge.global.base.BasePageRQ;
import com.geo.bridge.global.base.BasePageRS;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * DB 저장소에 클라이언트 정보 생성 서비스 로직
 * Domain 접근
 * 
 * <p>기능</p>
 * <ul>
 *     <li>{@link #createEmitterClientInfo(Mono)} 클라이언트 정보 생성</li>
 *     <li>{@link #getAllEmitterClientInfo(SearchEmitterClientInfoRQ, BasePageRQ)} 클라이언트 정보 생성</li>
 * </ul>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EmitterClientInfoService {

    private final EmitterClientService emitterClientService;

    /**
     * Emitter Client 데이터 생성
     * 1. Emitter Client Infomation DTO 변환
     * 2. DB Save 호출
     * @param mono Web RQ Model
     * @return
     */
    @Transactional
    public Mono<Void> createEmitterClientInfo(Mono<CreateEmitterClientInfoRQ> mono){
        return mono
            .map(CreateEmitterClientInfoRQ::toDto)
            .flatMap(dto -> emitterClientService.createClient(Mono.just(dto)))
            .then();
    }

    /**
     * Emitter Client 정보 리스트 조회 (페이징)
     * 1. Emitter Client DTO 객체 변환
     * 2. DB 데이터 호출
     * 3. Web RS 객체 변환
     * @param rq 검색 RQ
     * @param page 페이지네이션
     * @return
     */
    public Mono<BasePageRS<SearchEmitterClientInfoRS>> getAllEmitterClientInfo(SearchEmitterClientInfoRQ rq, BasePageRQ page){
        SearchEmitterDTO searchDTO = rq.toDto();
        Pageable pageable = PageRequest.of(page.getPage() - 1, page.getSize());
        
        return emitterClientService.getAllEmitterClient(searchDTO, pageable)
            .flatMap(pageRS ->
                 Flux.fromIterable(pageRS.getList())
                    .map(SearchEmitterClientInfoRS::fromDTO)
                    .collectList()
                    .map(newList -> 
                        BasePageRS.of(
                            newList, 
                            pageRS.getPage(), 
                            pageRS.getSize(), 
                            pageRS.getRange(), 
                            pageRS.getTotalCount()
                        )
                    )
            );
    }

}
