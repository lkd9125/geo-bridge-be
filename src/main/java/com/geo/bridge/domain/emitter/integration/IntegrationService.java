package com.geo.bridge.domain.emitter.integration;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.geo.bridge.domain.emitter.integration.client.EmitterClient;
import com.geo.bridge.domain.emitter.integration.client.MqttEmitterClient;
import com.geo.bridge.domain.emitter.integration.client.WsEmitterClient;
import com.geo.bridge.domain.emitter.integration.model.EmitterType;
import com.geo.bridge.global.exception.BaseException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * Emitter Client 통합서비스
 * 
 * <p>기능</p>
 * <ul>
 *     <li>{@link #createIntegrationEmitterClient(String, String, String, String, String, EmitterType)} 통합 클라이언트 생성</li>
 * </ul>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class IntegrationService {

    /**
     * 통합 클라이언트 생성
     * @param name 클라이언트 식별 명
     * @param host 호스트 정보
     * @param topic (옵션) 토픽
     * @param username (옵션) 연결 아이디
     * @param password (옵션) 연결 암호
     * @param type 클라이언트 타입
     * @param parameter 추가 파라미터
     * @return
     */
    public Mono<EmitterClient> createIntegrationEmitterClient(String name, String host, String topic, String username, String password, EmitterType type, Map<String, String> parameter){
        return switch(type){
            case MQTT -> Mono.just(new MqttEmitterClient(name, host, topic, username, password, parameter));
            case WS -> Mono.just(new WsEmitterClient(name, host, parameter));
            default -> throw new BaseException();
        };
    }
    
}
