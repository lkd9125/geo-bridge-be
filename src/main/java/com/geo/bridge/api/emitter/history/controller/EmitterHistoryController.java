package com.geo.bridge.api.emitter.history.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.geo.bridge.api.emitter.history.model.CreateHistoryRQ;
import com.geo.bridge.api.emitter.history.model.SearchHistoryRS;
import com.geo.bridge.api.emitter.history.service.EmitterHistoryService;
import com.geo.bridge.global.base.BasePageRQ;
import com.geo.bridge.global.base.BasePageRS;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * 실행 이력 API 컨트롤러
 *
 * <p>기능</p>
 * <ul>
 *     <li>{@link #saveHistory(Mono)} 실행 이력 저장</li>
 *     <li>{@link #getHistory(BasePageRQ)} 실행 이력 조회</li>
 *     <li>{@link #toggleFavorite(Long, String)} 실행 이력 즐겨찾기 변경</li>
 *     <li>{@link #deleteHistory(Long)} 실행 이력 삭제</li>
 * </ul>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/emitter/history")
public class EmitterHistoryController {

    private final EmitterHistoryService emitterHistoryService;

    /**
     * 실행 이력을 저장합니다.
     *
     * @param rq - 실행 이력 저장 요청
     * @return 저장 완료 시그널
     */
    @PostMapping
    public Mono<Void> saveHistory(@Valid @RequestBody Mono<CreateHistoryRQ> rq) {
        return emitterHistoryService.saveHistory(rq);
    }

    /**
     * 로그인 사용자의 실행 이력을 조회합니다.
     *
     * @param page - 페이지 정보
     * @return 실행 이력 페이지
     */
    @GetMapping("/plist")
    public Mono<BasePageRS<SearchHistoryRS>> getHistory(@Valid BasePageRQ page) {
        return emitterHistoryService.getHistory(page);
    }

    /**
     * 로그인 사용자의 실행 이력 즐겨찾기 상태를 변경합니다.
     *
     * @param sn - 실행 이력 PK
     * @param title - 변경할 실행 이력 제목
     * @return 변경된 즐겨찾기 상태
     */
    @PatchMapping("/favorite")
    public Mono<Boolean> toggleFavorite(@RequestParam("sn") Long sn, @RequestParam(value = "title", required = false) String title) {
        return emitterHistoryService.toggleFavorite(sn, title);
    }

    /**
     * 실행 이력을 삭제합니다.
     *
     * @param sn - 실행 이력 PK
     * @return 삭제 완료 시그널
     */
    @DeleteMapping
    public Mono<Void> deleteHistory(@RequestParam("sn") Long sn) {
        return emitterHistoryService.deleteHistory(sn);
    }

}
