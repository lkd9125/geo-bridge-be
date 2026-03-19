package com.geo.bridge.domain.emitter.context.model;

import lombok.Getter;

/**
 * Emitter Client 실행 상태.
 *
 * <ul>
 *  <li>시뮬레이터 Job의 현재 상태를 표현</li>
 *  <li>실행/중단/종료 흐름 및 모니터링 이벤트에 사용</li>
 * </ul>
 */
@Getter
public enum EmitterClientStatus {

    /** 대기 */
    WAIT, // 대기
    /** 실행 중 */
    PLAYING, // 활성화
    /** 연결 실패 */
    CONNECTED_FAIL, // 연결실패
    /** 종료 */
    END, // 종료
    ;
}
