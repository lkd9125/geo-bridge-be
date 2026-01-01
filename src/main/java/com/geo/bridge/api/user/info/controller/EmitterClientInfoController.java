package com.geo.bridge.api.user.info.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.geo.bridge.api.user.info.model.CreateEmitterClientInfoRQ;
import com.geo.bridge.api.user.info.model.SearchEmitterClientInfoRQ;
import com.geo.bridge.api.user.info.model.SearchEmitterClientInfoRS;
import com.geo.bridge.api.user.info.service.EmitterClientInfoService;
import com.geo.bridge.global.base.BasePageRQ;
import com.geo.bridge.global.base.BasePageRS;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * DB 저장소에 클라이언트 정보 생성 컨트롤러
 * Validation 처리
 * 
 * <p>기능</p>
 * <ul>
 *     <li>{@link #createEmitterClientInfo(Mono)} 클라이언트 정보 생성</li>
 *     <li>{@link #getAllEmitterClientInfo(SearchEmitterClientInfoRQ, BasePageRQ)} 클라이언트 정보 생성</li>
 * </ul>
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/emitter/client/info")
public class EmitterClientInfoController {

    private final EmitterClientInfoService emitterClientInfoService;

    /**
     * Emitter Client Infomation 생성 (DB)
     * @param rq Emitter Client 생성 객체
     * @return
     */
    @PostMapping
    public Mono<Void> createEmitterClientInfo(@Valid @RequestBody Mono<CreateEmitterClientInfoRQ> rq){
        return emitterClientInfoService.createEmitterClientInfo(rq);
    }

    /**
     * Emitter Client 정보 리스트 조회 (페이징)
     * @param rq 검색 객체
     * @param page 페이지네이션
     * @return
     */
    @GetMapping("/plist")
    public Mono<BasePageRS<SearchEmitterClientInfoRS>> getAllEmitterClientInfo(@Valid SearchEmitterClientInfoRQ rq, BasePageRQ page){
        return emitterClientInfoService.getAllEmitterClientInfo(rq, page);
    }

}
