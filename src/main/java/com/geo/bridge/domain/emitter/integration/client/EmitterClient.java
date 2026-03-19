package com.geo.bridge.domain.emitter.integration.client;

import java.util.Map;

import reactor.core.publisher.Mono;

/**
 * Emitter Client 객체 스펙
 * <p>기능</p>
 * <ul>
 *  <li>{@link #getName()} client 명칭 반환</li>
 *  <li>{@link #getHost()} host 반환</li>
 *  <li>{@link #getTopic()} topic 반환(옵션)</li>
 *  <li>{@link #connect()} host에 해당되는 연결</li>
 *  <li>{@link #disconnect()} client 연결 해제</li>
 *  <li>{@link #isConnected()} client 연결 확인</li>
 *  <li>{@link #send(String)} 데이터 전송</li>
 *  <li>{@link #getParameter()} 추가 파라미터</li>
 * </ul>
 */
public interface EmitterClient {
    /**
     * 클라이언트 식별명을 반환합니다.
     *
     * @return 클라이언트 명칭
     */
    String getName();

    /**
     * 연결 대상 호스트를 반환합니다.
     *
     * @return host 문자열
     */
    String getHost();

    /**
     * 전송 대상 토픽을 반환합니다(프로토콜에 따라 옵션).
     *
     * @return topic (없으면 null 가능)
     */
    String getTopic();

    /**
     * 대상 시스템과의 연결을 수행합니다.
     */
    void connect();

    /**
     * 대상 시스템과의 연결을 해제합니다.
     */
    void disconnect();

    /**
     * 연결 상태를 확인합니다.
     *
     * @return 연결되어 있으면 true
     */
    Boolean isConnected();

    /**
     * 데이터(payload)를 전송합니다.
     *
     * @param sendData 전송할 문자열
     * @return 전송 완료 시그널
     */
    Mono<Void> send(String sendData);

    /**
     * 클라이언트 생성 시 전달된 추가 파라미터를 반환합니다.
     *
     * @return 추가 파라미터 맵
     */
    Map<String, String> getParameter();
}
