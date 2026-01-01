package com.geo.bridge.domain.emitter.integration.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 외부 시스템(Drone, UTM 등)과 통신하기 위해 지원하는 전송 프로토콜 유형.
 * <p>
 * 이 Enum은 {@code UserHost} 설정에서 사용자가 선택 가능한 통신 방식을 정의하며,
 * {@code ClientFactory}에서 적절한 {@code OutboundPort} 구현체를 생성하는 기준으로 사용됩니다.
 * </p>
 *
 * @author GeoDeveloper
 * @since 1.0.0
 */
@Getter
@RequiredArgsConstructor
public enum EmitterType {

    /**
     * <b>MQTT (Message Queuing Telemetry Transport)</b>
     * <p>
     * 경량 메시징 프로토콜. 네트워크 대역폭이 제한적이거나 연결이 불안정한 환경(드론 등)에 적합.
     * Pub/Sub 구조를 사용함.
     * </p>
     */
    MQTT,

    /**
     * <b>TCP (Transmission Control Protocol)</b>
     * <p>
     * 1:1 연결 지향형 통신. 데이터의 신뢰성이 중요하고 실시간 스트리밍이 필요할 때 사용.
     * </p>
     */
    TCP,

    /**
     * <b>HTTP</b>
     * <p>
     * 단방향 요청/응답 구조. 상태 정보의 단순 리포팅이나 비동기 처리가 필요 없는 경우 사용.
     * </p>
     */
    HTTP,

    /**
     * <b>WS</b>
     * <p>
     * 웹 브라우저 기반의 클라이언트와 양방향 통신이 필요할 때 사용.
     * </p>
     */
    WS,
    ;    

}
