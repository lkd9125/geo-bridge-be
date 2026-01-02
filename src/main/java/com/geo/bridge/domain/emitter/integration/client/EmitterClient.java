package com.geo.bridge.domain.emitter.integration.client;

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
 * </ul>
 */
public interface EmitterClient {
    String getName();
    String getHost();
    String getTopic();
    void connect();
    void disconnect();
    Boolean isConnected();
    Mono<Void> send(String sendData);
}
