package com.geo.bridge.domain.simulator.history;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.geo.bridge.domain.simulator.history.dto.entity.History;
import com.geo.bridge.domain.simulator.history.repository.HistoryRepository;
import com.geo.bridge.global.base.BasePageRQ;
import com.geo.bridge.global.base.BasePageRS;
import com.geo.bridge.global.exception.BaseException;
import com.geo.bridge.global.exception.ExceptionCode;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * 실행 이력 서비스
 * 
 * <p>기능</p>
 * <ul>
 *     <li>{@link #saveHistory(Mono)} 실행 이력 저장</li>
 *     <li>{@link #getHistory(String, BasePageRQ)} 실행 이력 조회</li>
 *     <li>{@link #toggleFavorite(Long, String)} 실행 이력 즐겨찾기 변경</li>
 *     <li>{@link #deleteHistory(Long)} 실행 이력 삭제</li>
 * </ul>
 */
@Service 
@RequiredArgsConstructor 
public class HistoryService {

    private final HistoryRepository historyRepository;

    /**
     * 실행 이력을 저장합니다.
     * - 즐겨찾기 여부와 생성일시 기본값을 설정합니다.
     * - 실행 이력을 데이터베이스에 저장합니다.
     *
     * @param mono - 저장할 실행 이력
     * @return 저장 완료 시그널
     */
    public Mono<Void> saveHistory(Mono<History> mono) {
        return mono
            .map(history -> {
                if (history.getFavorite() == null) {
                    history.setFavorite(false);
                }
                if (history.getCreateDt() == null) {
                    history.setCreateDt(LocalDateTime.now());
                }
                return history;
            })
            .flatMap(historyRepository::save)
            .then();
    }

    /**
     * 사용자별 실행 이력을 조회합니다.
     * - 즐겨찾기 여부와 즐겨찾기 일시 내림차순으로 조회합니다.
     * - 조회 목록과 전체 개수를 페이징 응답으로 변환합니다.
     *
     * @param username - 사용자 PK 필수값
     * @param page - 페이지 정보
     * @return 실행 이력 페이지
     */
    public Mono<BasePageRS<History>> getHistory(String username, BasePageRQ page) {
        if (username == null || username.isBlank()) {
            return Mono.error(new BaseException(ExceptionCode.PARAMETER_INVALID, "username은 필수값입니다."));
        }

        Pageable pageable = PageRequest.of(page.getPage() - 1, page.getSize());
        Mono<List<History>> listMono = historyRepository.findByUsername(username, pageable).collectList();
        Mono<Long> countMono = historyRepository.countByUsername(username);

        return Mono.zip(listMono, countMono)
            .map(tuple -> BasePageRS.of(
                tuple.getT1(),
                page.getPage(),
                page.getSize(),
                page.getRange(),
                tuple.getT2().intValue()
            ));
    }

    /**
     * 사용자 소유 실행 이력의 즐겨찾기 상태를 변경합니다.
     * - 실행 이력 PK와 사용자 PK로 본인 이력 여부를 확인합니다.
     * - 즐겨찾기 상태와 즐겨찾기 일시를 함께 변경합니다.
     *
     * @param sn - 실행 이력 PK
     * @param username - 사용자 PK
     * @return 변경된 즐겨찾기 상태
     */
    @Transactional
    public Mono<Boolean> toggleFavorite(Long sn, String username) {
        if (sn == null || username == null || username.isBlank()) {
            return Mono.error(new BaseException(ExceptionCode.PARAMETER_INVALID));
        }

        return historyRepository.findBySnAndUsername(sn, username)
            .switchIfEmpty(Mono.error(new BaseException(ExceptionCode.NOT_SERACH_DATA)))
            .flatMap(history -> {
                boolean favorite = !Boolean.TRUE.equals(history.getFavorite());
                history.setFavorite(favorite);
                history.setFavoriteDt(favorite ? LocalDateTime.now() : null);

                return historyRepository.save(history);
            })
            .map(History::getFavorite);
    }

    /**
     * 실행 이력을 삭제합니다.
     *
     * @param sn - 실행 이력 PK
     * @return 삭제 완료 시그널
     */
    @Transactional
    public Mono<Void> deleteHistory(Long sn) {
        return historyRepository.deleteById(sn);
    }

}
