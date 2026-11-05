package com.geo.bridge.api.emitter.history.service;

import org.springframework.stereotype.Service;

import com.geo.bridge.api.emitter.history.model.CreateHistoryRQ;
import com.geo.bridge.api.emitter.history.model.SearchHistoryRS;
import com.geo.bridge.domain.simulator.history.HistoryService;
import com.geo.bridge.global.base.BasePageRQ;
import com.geo.bridge.global.base.BasePageRS;
import com.geo.bridge.global.security.SecurityHelper;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 실행 이력 API 서비스
 *
 * <p>기능</p>
 * <ul>
 *     <li>{@link #saveHistory(Mono)} 실행 이력 저장</li>
 *     <li>{@link #getHistory(BasePageRQ)} 실행 이력 조회</li>
 *     <li>{@link #deleteHistory(Long)} 실행 이력 삭제</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class EmitterHistoryService {

    private final HistoryService historyService;

    /**
     * 실행 이력을 저장합니다.
     * - 요청 모델을 실행 이력 엔티티로 변환합니다.
     * - 실행 이력 도메인 서비스를 호출합니다.
     *
     * @param mono - 실행 이력 저장 요청
     * @return 저장 완료 시그널
     */
    public Mono<Void> saveHistory(Mono<CreateHistoryRQ> mono) {
        return mono
            .flatMap(CreateHistoryRQ::toDto)
            .flatMap(history -> historyService.saveHistory(Mono.just(history)));
    }

    /**
     * 로그인 사용자의 실행 이력을 조회합니다.
     * - 인증 정보에서 사용자 PK를 조회합니다.
     * - 도메인 조회 결과를 API 응답 모델로 변환합니다.
     *
     * @param page - 페이지 정보
     * @return 실행 이력 페이지
     */
    public Mono<BasePageRS<SearchHistoryRS>> getHistory(BasePageRQ page) {
        return SecurityHelper.securityHolder()
            .flatMap(user -> historyService.getHistory(user.getUsername(), page))
            .flatMap(pageRS ->
                Flux.fromIterable(pageRS.getList())
                    .map(SearchHistoryRS::fromDTO)
                    .collectList()
                    .map(list -> BasePageRS.of(
                        list,
                        pageRS.getPage(),
                        pageRS.getSize(),
                        pageRS.getRange(),
                        pageRS.getTotalCount()
                    ))
            );
    }

    /**
     * 실행 이력을 삭제합니다.
     *
     * @param sn - 실행 이력 PK
     * @return 삭제 완료 시그널
     */
    public Mono<Void> deleteHistory(Long sn) {
        return historyService.deleteHistory(sn);
    }

}
